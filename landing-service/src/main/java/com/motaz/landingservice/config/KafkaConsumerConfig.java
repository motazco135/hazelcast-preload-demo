package com.motaz.landingservice.config;

import com.motaz.landingservice.dto.AccountDto;
import com.motaz.landingservice.dto.CustomerProfileDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig extends KafkaProperties.Consumer {

    private final KafkaProperties common;

    @Bean
    public ConsumerFactory<String, CustomerProfileDto> customerProfileConsumerFactory() {
        final var conf = new HashMap<>(this.common.buildConsumerProperties());
        Map<String, Object> props = new HashMap<>(conf);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
        JacksonJsonDeserializer<CustomerProfileDto> valueDeserializer = new JacksonJsonDeserializer<>(CustomerProfileDto.class);
        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    @Bean
    public ConsumerFactory<String, AccountDto> accountConsumerFactory() {
        final var conf = new HashMap<>(this.common.buildConsumerProperties());
        Map<String, Object> props = new HashMap<>(conf);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
        JacksonJsonDeserializer<AccountDto> valueDeserializer = new JacksonJsonDeserializer<>(AccountDto.class);
        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    @Bean(name = "accountKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, AccountDto> accountKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AccountDto> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(accountConsumerFactory());
        ContainerProperties props = factory.getContainerProperties();
        props.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setConcurrency(3);
        factory.getContainerProperties().setSyncCommits(true);
        return factory;
    }

    @Bean(name = "customerProfileKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, CustomerProfileDto> customerProfileKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CustomerProfileDto> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(customerProfileConsumerFactory());
        ContainerProperties props = factory.getContainerProperties();
        props.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setConcurrency(3);
        factory.getContainerProperties().setSyncCommits(true);
        return factory;
    }
}
