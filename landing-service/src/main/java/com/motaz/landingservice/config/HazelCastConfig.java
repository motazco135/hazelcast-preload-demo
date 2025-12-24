package com.motaz.landingservice.config;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.motaz.landingservice.dto.LandingDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class HazelCastConfig {

    @Value("${hazelcast-map.ods}")
    private String landingMap;

    @Bean
    public IMap<String, LandingDto> map(HazelcastInstance instance) {
        log.info(landingMap);
        return instance.getMap(landingMap);
    }
}
