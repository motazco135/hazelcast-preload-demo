package com.motaz.profileservice.service;

import com.motaz.profileservice.dto.CustomerProfileDto;
import com.motaz.profileservice.events.CustomerProfileCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerEventProducer {

    private final KafkaTemplate<String, CustomerProfileDto> kafkaTemplate;

    @Value("${spring.kafka.topic.customer-profile}")
    private String customerProfileTopic;

    public void publishCustomerProfile(CustomerProfileDto customerProfileDto) {
        String key = String.valueOf(customerProfileDto.getCustomerId());

        log.info("Publishing customer profile to topic: {} with key: {}", customerProfileTopic, key);
        
        CompletableFuture<SendResult<String, CustomerProfileDto>> future =
            kafkaTemplate.send(customerProfileTopic, key, customerProfileDto);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Successfully published customer profile [ID={}] to topic: {} partition: {} offset: {}",
                        customerProfileDto.getCustomerId(),
                        customerProfileTopic,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish customer profile [ID={}] to topic: {}",
                        customerProfileDto.getCustomerId(),
                        customerProfileTopic,
                        ex);
            }
        });
    }
}
