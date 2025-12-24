package com.motaz.accountservice.consumer;

import com.motaz.accountservice.dto.CustomerProfileDto;
import com.motaz.accountservice.service.CustomerOnboardingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerProfileConsumer {

    private final CustomerOnboardingService customerOnboardingService;

    @KafkaListener(topics = "#{'${spring.kafka.consumer.customer-profile.topic}'}",
            groupId = "#{'${spring.kafka.consumer.group-id}'}",
            containerFactory = "customerProfileKafkaListenerContainerFactory"
    )
    public void consumeCustomerProfile(@Payload CustomerProfileDto customerProfileDto, Acknowledgment acknowledgment) {
        try {
            log.info("Received customer profile: {}", customerProfileDto);
            //Create a Customer default account
            customerOnboardingService.handleCustomerProfile(customerProfileDto);
            acknowledgment.acknowledge();
            log.info("Successfully processed customer profile for customer: {}", customerProfileDto.getCustomerId());
        } catch (Exception e) {
            log.error("Error processing customer profile: {}", customerProfileDto, e);
        }
    }
}
