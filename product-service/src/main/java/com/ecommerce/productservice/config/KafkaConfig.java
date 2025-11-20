package com.ecommerce.productservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic inventoryReservationRequestTopic() {
        return TopicBuilder.name("inventory-reservation-request")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic inventoryReservationResultTopic() {
        return TopicBuilder.name("inventory-reservation-result")
                .partitions(3)
                .replicas(1)
                .build();
    }
}

