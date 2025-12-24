package com.motaz.landingservice.service;

import com.motaz.landingservice.dto.LandingDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.quota.ClientQuotaAlteration;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LandingService {

    private final CacheService cacheService;
    private final ExternalApiClient externalApiClient;

    // 1. get customer cache-miss
    public Optional<LandingDto> getCustomerCacheMiss(Long customerId){
        long startTime = System.currentTimeMillis();
        // 1. get Customer form Cache only
        Optional<LandingDto> optionalLandingDto = cacheService.getCustomerFromCacheOnly(customerId);
        long cacheReadTime = System.currentTimeMillis() - startTime;
        if(optionalLandingDto.isPresent()){
            log.info("customer Data found in cache for customer :{}",customerId);
            return optionalLandingDto;
        }
        // 2. not found get customer from ods
        log.info("Customer data not found in cache for customer ID: {} (cache read took {} ms), fetching from services",
                customerId, cacheReadTime);
        optionalLandingDto = cacheService.getCustomerFromOdsOnly(customerId);
        if (optionalLandingDto.isPresent()) {
            long odsReadTime = System.currentTimeMillis() - startTime;
            log.info("Customer data not found in cache for customer ID: {} (cache read took {} ms), fetching from ods",
                    customerId, odsReadTime);
            // add to cache
            cacheService.put(customerId,optionalLandingDto.get());
            return optionalLandingDto;
        }
        // 3. not found get customer from fanout
        LandingDto  landingDto = externalApiClient.fetchAndCacheCustomerData(customerId);
        long fanOutReadTime = System.currentTimeMillis() - startTime;
        log.info("Customer data not found in ods for customer ID: {} (cache read took {} ms), fetching from fan-out API",
                customerId, fanOutReadTime);
        return Optional.of(landingDto);
    }

    public Optional<LandingDto> preLoadCustomer(Long customerId) {
        long startTime = System.currentTimeMillis();
        // 1. get Customer form Cache only
        Optional<LandingDto> optionalLandingDto = cacheService.getCustomerFromCacheOnly(customerId);
        if(optionalLandingDto.isPresent()){
            long cacheReadTime = System.currentTimeMillis() - startTime;
            log.info(" preLoadCustomer Customer Data for customer ID: {} in {} ms from cache ", customerId, cacheReadTime);
            return optionalLandingDto;
        }
        // 2.get customer from odsonly
        optionalLandingDto = cacheService.getCustomerFromOdsOnly(customerId);
        if(optionalLandingDto.isPresent()){
            // add to cache
            cacheService.put(customerId,optionalLandingDto.get());
            long odsReadTime = System.currentTimeMillis() - startTime;
            log.info(" preLoadCustomer Customer Data for customer ID: {} in {} ms from ODS ", customerId, odsReadTime);
            return optionalLandingDto;
        }
        return Optional.empty();
    }


    // 2. get from cach only
    public Optional<LandingDto> getCustomerFromCacheOnly(Long customerId){
        return cacheService.getCustomerFromCacheOnly(customerId);
    }

    // 3. get from ODS only
    public Optional<LandingDto> getCustomerFromOdsOnly(Long customerId){
        return cacheService.getCustomerFromOdsOnly(customerId);
    }

    // 4. get from fanout
    public LandingDto getCustomerFanOut(Long customerId){
        long startTime = System.currentTimeMillis();
        // Fetch from services and cache it
        LandingDto landingDto = externalApiClient.fetchAndCacheCustomerData(customerId);
        long cacheReadTime = System.currentTimeMillis() - startTime;
        log.info("fanOutCustomer Customer Data for customer ID: {} in {} ms", customerId, cacheReadTime);
        return landingDto;
    }

}
