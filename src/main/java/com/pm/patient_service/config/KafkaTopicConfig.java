package com.pm.patient_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class KafkaTopicConfig {
	private static final Logger log = LoggerFactory.getLogger(KafkaTopicConfig.class);
    @Bean
    public NewTopic patientTopic() {
		log.error(">>> Entering patientTopic()");
        return TopicBuilder.name("patient")
                .partitions(3)
                .replicas(1)
                .build();
    }

}
