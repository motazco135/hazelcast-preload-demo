package com.motaz.accountservice.service;

import com.motaz.accountservice.dto.AccountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountEventProducer {

    private final KafkaTemplate<String, AccountDto> kafkaTemplate;

    @Value("${spring.kafka.topic.customer-account}")
    private String customerAccountTopic;

    public void publishAccount(AccountDto accountDto) {
        String key = String.valueOf(accountDto.getIban());

        log.info("Publishing account to topic: {} with key: {}", accountDto, key);

        CompletableFuture<SendResult<String, AccountDto>> future =
                kafkaTemplate.send(customerAccountTopic, key, accountDto);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Successfully published account [ID={}] to topic: {} partition: {} offset: {}",
                        accountDto.getIban(),
                        customerAccountTopic,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish customer profile [ID={}] to topic: {}",
                        accountDto.getIban(),
                        customerAccountTopic,
                        ex);
            }
        });
    }

}
