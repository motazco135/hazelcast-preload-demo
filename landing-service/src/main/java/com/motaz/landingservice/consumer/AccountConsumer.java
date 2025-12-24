package com.motaz.landingservice.consumer;

import com.motaz.landingservice.dto.AccountDto;
import com.motaz.landingservice.service.EventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountConsumer {

    private final EventProcessor aggregator;

    @KafkaListener(topics = "#{'${spring.kafka.consumer.customer-account.topic}'}",
            groupId = "#{'${spring.kafka.consumer.customer-account.group-id}'}",
            containerFactory = "accountKafkaListenerContainerFactory"
    )
    public void consumeAccount(@Payload AccountDto accountDto, Acknowledgment acknowledgment) {
        try {
            log.info("Received Account: {}", accountDto);

            aggregator.upsertAccount(accountDto);

            acknowledgment.acknowledge();
            log.info("Successfully processed customer account for account_id: {} , IABN: {}", accountDto.getAccountId(),accountDto.getIban());
        } catch (Exception e) {
            log.error("Error processing customer account: {}", accountDto, e);
        }
    }
}
