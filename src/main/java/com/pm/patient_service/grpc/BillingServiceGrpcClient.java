package com.pm.patient_service.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import billing.BillingServiceGrpc.BillingServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceGrpcClient {
    private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpcClient.class);
    private final BillingServiceBlockingStub blockingStub;

    public BillingServiceGrpcClient(
            @Value("${billing.service.address:localhost}") String serverAddress,
            @Value("${billing.service.grpc.port:9001}") int serverPort
    ) {
		log.debug(">>> Entering BillingServiceGrpcClient(serverAddress={},serverPort={})", serverAddress, serverPort);
        log.info("Connecting to Billing Service GRPC service at {}:{}", serverAddress, serverPort);
        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress,
                serverPort).usePlaintext().build();

        blockingStub = BillingServiceGrpc.newBlockingStub(channel);
		log.debug("<<< Exiting BillingServiceGrpcClient(serverAddress={},serverPort={})", serverAddress, serverPort);
    }
    public BillingResponse createBillingAccount(String patientId, String name, String email) {
	log.debug(">>> Entering createBillingAccount(patientId={},name={},email={})", patientId, name, email);

        BillingRequest request = BillingRequest.newBuilder().setPatientId(patientId)
                .setName(name).setEmail(email).build();

        BillingResponse response = blockingStub.createBillingAccount(request);
		log.debug("createBillingAccount(patientId={},name={},email={}): response → {}", patientId, name, email, response);
        log.info("Received response from billing service via GRPC: {}", response);
		log.debug("<<< Exiting createBillingAccount(patientId={},name={},email={})", patientId, name, email);
        return response;
    }
}
