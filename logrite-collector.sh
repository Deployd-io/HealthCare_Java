#!/usr/bin/env bash
# Logrite Collector Agent. Zero dependency: curl + coreutils. Headless by default.
# Token is read from $LOGRITE_AGENT_TOKEN (never embedded in this file).
# Capabilities are baked below; override per run with LOGRITE_* env vars.
set -uo pipefail
AGENT_KEY="collector-healthcarerah"
INGEST_URL="https://ingest.logrite.ai/ingest"
INTERVAL_MIN=15
AGENT_VERSION="1.1.0"
COLLECT_LOGS="${LOGRITE_COLLECT_LOGS:-1}"
COLLECT_SYSTEM="${LOGRITE_COLLECT_SYSTEM:-1}"
HEARTBEAT="${LOGRITE_HEARTBEAT:-1}"
COLLECT_PATH="${LOGRITE_COLLECT_PATH:-/var/log/healthcareRah}"
# resolve to an absolute path so scheduled (cron) runs watch the same folder
_abs="$(cd "$COLLECT_PATH" 2>/dev/null && pwd)"; COLLECT_PATH="${_abs:-$COLLECT_PATH}"
STATE_DIR="$HOME/.logrite-collector"
STATE_FILE="$STATE_DIR/$AGENT_KEY.state"
REVOKED_FILE="$STATE_DIR/$AGENT_KEY.revoked"
mkdir -p "$STATE_DIR"; chmod 700 "$STATE_DIR" 2>/dev/null || true
touch "$STATE_FILE"; chmod 600 "$STATE_FILE"
# absolute path to THIS script (used for cron dedup + clean self-uninstall)
SELF="$(cd "$(dirname "$0")" 2>/dev/null && pwd)/$(basename "$0")"; [ -f "$SELF" ] || SELF="$0"

# Remove every trace of THIS collector from the host: its cron line (matched by
# this script's path so other agents are untouched), its state + revocation files,
# the state dir if now empty, and the script file itself.
do_uninstall() {
  if command -v crontab >/dev/null 2>&1; then
    ( crontab -l 2>/dev/null | grep -vF "$SELF --loop" ) | crontab - 2>/dev/null || true
  fi
  rm -f "$STATE_FILE" "$REVOKED_FILE" 2>/dev/null || true
  rmdir "$STATE_DIR" 2>/dev/null || true
  rm -f "$SELF" 2>/dev/null || true
  echo "Logrite collector '$AGENT_KEY' uninstalled — no cron entry, script, or state remains."
}
# Manual uninstall needs no token:  bash logrite-collector.sh --uninstall
if [ "${1:-}" = '--uninstall' ]; then do_uninstall; exit 0; fi

if [ -z "${LOGRITE_AGENT_TOKEN:-}" ]; then echo 'ERROR: set LOGRITE_AGENT_TOKEN'; exit 1; fi

menu() {
  echo 'Logrite Collector — capabilities (chosen when the agent was created):'
  echo "  logs=$COLLECT_LOGS  system_details=$COLLECT_SYSTEM  heartbeat=$HEARTBEAT  path=$COLLECT_PATH"
  echo '  Other source profiles (Local Claude folders, journald / /var/log) are coming soon.'
}

# offset state: one line per file -> 'inode<TAB>byteoffset'
get_offset() { awk -v ino="$1" '$1==ino{print $2}' "$STATE_FILE" | tail -1; }
set_offset() { local ino="$1" off="$2"; grep -v "^$ino " "$STATE_FILE" > "$STATE_FILE.tmp" 2>/dev/null || true; echo "$ino $off" >> "$STATE_FILE.tmp"; mv "$STATE_FILE.tmp" "$STATE_FILE"; chmod 600 "$STATE_FILE"; }

json_escape() { sed -e 's/\\/\\\\/g' -e 's/\"/\\\"/g'; }

ship_file() {
  local f="$1"; [ -f "$f" ] || return 0
  local ino size off; ino=$(ls -i "$f" | awk '{print $1}'); size=$(wc -c < "$f")
  off=$(get_offset "$ino"); off="${off:-0}"
  if [ "$size" -lt "$off" ]; then off=0; fi          # rotated/truncated
  [ "$size" -le "$off" ] && return 0                  # nothing new
  local batch; batch=$(mktemp)
  tail -c +$((off+1)) "$f" | while IFS= read -r line; do
    [ -z "$line" ] && continue
    printf '{\"message\":\"%s\",\"filePath\":\"%s\"}\n' "$(printf '%s' "$line" | json_escape)" "$(printf '%s' "$f" | json_escape)" >> "$batch"
  done
  if [ -s "$batch" ]; then
    if curl -fsS -m 30 -X POST "$INGEST_URL" -H "Authorization: Bearer $LOGRITE_AGENT_TOKEN" -H 'Content-Type: application/x-ndjson' --data-binary @"$batch" >/dev/null; then
      set_offset "$ino" "$size"
    else
      echo "warn: ingest failed for $f (offset preserved, will retry)"
    fi
  fi
  rm -f "$batch"
}

scan_once() { find "$COLLECT_PATH" -maxdepth 1 -type f \( -name '*.log' -o -name '*.txt' -o -name '*.jsonl' -o -name '*.out' \) 2>/dev/null | while IFS= read -r f; do ship_file "$f"; done; }

# system details: one host-metadata doc (no root). type:system_metrics is
# stored (drives M-26-14 Inventory) but excluded from log line/byte counts.
collect_system_details() {
  local hn os kern arch cpus mem disk dfree up doc
  hn="$(hostname 2>/dev/null || uname -n)"
  os="$(uname -s)"
  if [ -r /etc/os-release ]; then os="$(. /etc/os-release 2>/dev/null; printf '%s' "${PRETTY_NAME:-$os}")"; fi
  kern="$(uname -r)"; arch="$(uname -m)"
  if command -v nproc >/dev/null 2>&1; then cpus="$(nproc)"; elif [ -r /proc/cpuinfo ]; then cpus="$(grep -c ^processor /proc/cpuinfo)"; else cpus="$(sysctl -n hw.ncpu 2>/dev/null || echo 0)"; fi
  if [ -r /proc/meminfo ]; then mem="$(awk '/MemTotal/{print $2*1024; exit}' /proc/meminfo)"; else mem="$(sysctl -n hw.memsize 2>/dev/null || echo 0)"; fi
  disk="$(df -Pk . 2>/dev/null | awk 'NR==2{print $2*1024; exit}')"; dfree="$(df -Pk . 2>/dev/null | awk 'NR==2{print $4*1024; exit}')"
  if [ -r /proc/uptime ]; then up="$(awk '{print int($1); exit}' /proc/uptime)"; else up=0; fi
  doc="$(printf '{\"type\":\"system_metrics\",\"hostname\":\"%s\",\"os\":\"%s\",\"kernel\":\"%s\",\"arch\":\"%s\",\"cpus\":%s,\"memBytes\":%s,\"diskBytes\":%s,\"diskFreeBytes\":%s,\"uptimeSec\":%s,\"agentVersion\":\"%s\"}' "$(printf '%s' "$hn" | json_escape)" "$(printf '%s' "$os" | json_escape)" "$(printf '%s' "$kern" | json_escape)" "$(printf '%s' "$arch" | json_escape)" "${cpus:-0}" "${mem:-0}" "${disk:-0}" "${dfree:-0}" "${up:-0}" "$AGENT_VERSION")"
  if curl -fsS -m 30 -X POST "$INGEST_URL" -H "Authorization: Bearer $LOGRITE_AGENT_TOKEN" -H "X-Collector-Host: $hn" -H 'Content-Type: application/x-ndjson' --data-binary "$doc"$'\n' >/dev/null; then :; else echo 'warn: system_metrics ship failed (will retry next cycle)'; fi
}

# check-in: ONE auth-gated request per cycle. Doubles as the liveness ping AND
# the self-uninstall trigger — we read the HTTP status (no -f) to tell apart:
#   401 -> token revoked (agent deleted/rotated) => self-uninstall after 2 confirms
#   403 -> agent disabled on the server          => pause this cycle (NOT removed)
#   2xx -> healthy                               => proceed
# heartbeat ON sends type:heartbeat (bumps last-seen so an idle agent still shows
# active); OFF sends type:checkin (auth-only, not indexed, no last-seen bump).
checkin() {
  local t doc
  if [ "$HEARTBEAT" = "1" ]; then t="heartbeat"; else t="checkin"; fi
  doc="$(printf '{\"type\":\"%s\",\"agentVersion\":\"%s\",\"ts\":\"%s\"}' "$t" "$AGENT_VERSION" "$(date -u +%Y-%m-%dT%H:%M:%SZ)")"
  curl -s -o /dev/null -w '%{http_code}' -m 15 -X POST "$INGEST_URL" -H "Authorization: Bearer $LOGRITE_AGENT_TOKEN" -H 'Content-Type: application/x-ndjson' --data-binary "$doc"$'\n'
}

reset_revoked() { rm -f "$REVOKED_FILE" 2>/dev/null || true; }
on_revoked() {
  local n; n=$(cat "$REVOKED_FILE" 2>/dev/null || echo 0); n=$((n+1)); echo "$n" > "$REVOKED_FILE" 2>/dev/null || true
  if [ "$n" -ge 2 ]; then
    echo 'Token revoked on the server — uninstalling this collector (no trace left).'
    do_uninstall
  else
    echo "warn: token rejected ($n/2) — confirming once more before self-uninstall."
  fi
}

run_cycle() {
  local code; code="$(checkin)"
  case "$code" in
    401) on_revoked; return 0 ;;
    403) echo 'Agent disabled on the server — paused (no data sent).'; return 0 ;;
    000) echo 'warn: ingest endpoint unreachable (will retry next cycle).'; return 0 ;;
    *) reset_revoked ;;
  esac
  [ "$COLLECT_LOGS" = "1" ] && scan_once
  [ "$COLLECT_SYSTEM" = "1" ] && collect_system_details
  return 0
}

case "${1:-}" in
  --menu) menu; exit 0 ;;
  --loop) run_cycle; exit 0 ;;
esac
echo "Logrite collector active for $AGENT_KEY -> $INGEST_URL (every ${INTERVAL_MIN}m) [logs=$COLLECT_LOGS system=$COLLECT_SYSTEM heartbeat=$HEARTBEAT path=$COLLECT_PATH]"
run_cycle
# self-schedule: prefer cron, else nohup loop (no root needed)
if [ "${1:-}" != '--loop' ]; then
  if command -v crontab >/dev/null 2>&1; then
    LINE="*/$INTERVAL_MIN * * * * LOGRITE_AGENT_TOKEN=$LOGRITE_AGENT_TOKEN LOGRITE_COLLECT_PATH='$COLLECT_PATH' bash $SELF --loop >/dev/null 2>&1"
    ( crontab -l 2>/dev/null | grep -vF "$SELF --loop"; echo "$LINE" ) | crontab - && echo 'Scheduled via cron.' || echo 'Could not schedule via cron (run with --loop manually).'
  else
    ( while true; do sleep $((INTERVAL_MIN*60)); LOGRITE_AGENT_TOKEN="$LOGRITE_AGENT_TOKEN" LOGRITE_COLLECT_PATH="$COLLECT_PATH" bash "$SELF" --loop; done ) >/dev/null 2>&1 &
    echo "Scheduled via nohup loop (pid $!)."
  fi
fi
