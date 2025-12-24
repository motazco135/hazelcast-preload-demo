package com.motaz.landingservice;

import com.hazelcast.core.HazelcastInstance;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LandingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LandingServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner hzSmoke(HazelcastInstance hz) {
        return args -> {
            var map = hz.getMap("landing-map");
            map.put("mc-test", "ok"); // forces creation + entry
            System.out.println("HZ cluster=" + hz.getConfig().getClusterName());
            System.out.println("HZ members=" + hz.getCluster().getMembers());
        };
    }
}
