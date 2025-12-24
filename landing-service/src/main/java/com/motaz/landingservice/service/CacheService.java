package com.motaz.landingservice.service;

import com.hazelcast.map.IMap;
import com.motaz.landingservice.dto.AccountDto;
import com.motaz.landingservice.dto.CustomerProfileDto;
import com.motaz.landingservice.dto.LandingDto;
import com.motaz.landingservice.model.OdsEntity;
import com.motaz.landingservice.repository.OdsLandingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import javax.swing.text.html.Option;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheService {

    private final IMap<String, LandingDto> landingDtoIMap;
    private final ObjectMapper objectMapper;
    private final OdsLandingRepository odsRepository;

    public Optional<LandingDto> getCustomerFromCacheOnly(Long customerId){
        return Optional.ofNullable(landingDtoIMap.get(key(customerId)));
    }

    public Optional<LandingDto> getCustomerFromOdsOnly(Long customerId){
        return Optional.ofNullable(odsRepository.findById(customerId)
                .map(ods -> {
                    try {
                        LandingDto dto = objectMapper.readValue(ods.getBundleJson(), LandingDto.class);
                        // Populate cache from DB
                        landingDtoIMap.put(key(customerId), dto);
                        log.debug("Loaded customer {} from ODS and cached", customerId);
                        return dto;
                    } catch (Exception e) {
                        log.error("Failed to deserialize ODS data for customer {}", customerId, e);
                        return null;
                    }
                })
                .orElse(null)); // Returns null if not in DB either);
    }


    public void upsertCustomerProfile(Long customerId, CustomerProfileDto customerProfileDto){
        Optional<LandingDto> optionalLandingDto = getCustomerFromOdsOnly(customerId);
        LandingDto landingDto ;
        if(optionalLandingDto.isEmpty()){
            landingDto = new LandingDto();
            landingDto.setCustomerId(customerId);
        }else{
             landingDto = optionalLandingDto.get();
        }
        landingDto.setCustomerProfileDto(customerProfileDto);
        landingDto.setLastUpdatedAt(Instant.now());
        // Save to both cache and database
        saveToOds(customerId, landingDto);

        // update cache if found
        Optional<LandingDto>  optionalCachedLandingDto = getCustomerFromCacheOnly(customerProfileDto.getCustomerId());
        if(optionalLandingDto.isPresent()){
            // update the cache
            LandingDto cachedLandingDto =  optionalLandingDto.get();
            cachedLandingDto.setCustomerProfileDto(customerProfileDto);
            put(customerProfileDto.getCustomerId(),cachedLandingDto);
        }
    }

    public void upsertAccount(Long customerId, AccountDto accountDto) {
        Optional<LandingDto> optionalLandingDto = getCustomerFromOdsOnly(accountDto.getCustomerId());
        // Filter out existing account with same IBAN and replace with new one
        if(!optionalLandingDto.isEmpty()){
            LandingDto landingDto = optionalLandingDto.get();
            if (landingDto.getAccounts() != null) {
                landingDto.getAccounts().removeIf(acc ->
                        acc != null && acc.getIban() != null && acc.getIban().equals(accountDto.getIban())
                );
            }else{
                landingDto.setAccounts(new ArrayList<>());
            }
            landingDto.getAccounts().add(accountDto);
            landingDto.setCustomerId(accountDto.getCustomerId());
            landingDto.setLastUpdatedAt(Instant.now());

            // Save to both cache and database
            saveToOds(customerId, landingDto);

            // 2. check the cache if found update the cache
            Optional<LandingDto>  cachedOptionalLandingDto = getCustomerFromCacheOnly(accountDto.getCustomerId());
            if(cachedOptionalLandingDto.isPresent()){
                // update the cache
                LandingDto cachedLandingDto =  cachedOptionalLandingDto.get();
                if (cachedLandingDto.getAccounts() != null) {
                    cachedLandingDto.getAccounts().removeIf(acc ->
                            acc != null && acc.getIban() != null && acc.getIban().equals(accountDto.getIban())
                    );
                }
                cachedLandingDto.getAccounts().add(accountDto);
                put(accountDto.getCustomerId(),cachedLandingDto);
            }
        }
    }

    // Save to cache
    public void put(Long customerId, LandingDto landingDto){
        landingDtoIMap.put(key(customerId), landingDto, 10, TimeUnit.SECONDS, 5, TimeUnit.SECONDS);
    }

    private String key(Long customerId) {
        return "landing:" + customerId;
    }

    @Transactional
    public void saveToOds(Long customerId, LandingDto landingDto) {
        // Write to ODS
        try {
            OdsEntity e = odsRepository.findById(customerId)
                    .orElseGet(() -> {
                        OdsEntity n = new OdsEntity();
                        n.setCustomerId(customerId);
                        return n;
                    });
            e.setBundleJson(objectMapper.writeValueAsString(landingDto));
            odsRepository.save(e); // insert if new, update if exists
            log.debug("Saved customer {} to ODS table", customerId);
        } catch (Exception e) {
            log.error("Failed to serialize LandingDto for customer {}", customerId, e);
            throw new RuntimeException("Failed to save to ODS", e);
        }
    }

    // Central method to save to both Hazelcast and PostgreSQL
    @Transactional
    public void saveToHazelcastAndDb(Long customerId, LandingDto landingDto) {
        // 1. Write to Hazelcast
        landingDtoIMap.put(key(customerId), landingDto, 10, TimeUnit.SECONDS, 5, TimeUnit.SECONDS);
        // 2. Write to PostgreSQL
        saveToOds(customerId, landingDto);
    }

    // Load customer data from ODS and cache it
    private Optional<LandingDto> loadFromOds(Long customerId) {
        return odsRepository.findById(customerId)
                .map(ods -> deserializeAndCache(customerId, ods));
    }

    private LandingDto deserializeAndCache(Long customerId, OdsEntity ods) {
        try {
            LandingDto dto = objectMapper.readValue(ods.getBundleJson(), LandingDto.class);
            landingDtoIMap.put(key(customerId), dto);
            log.debug("Loaded customer {} from ODS and cached", customerId);
            return dto;
        } catch (Exception e) {
            log.error("Failed to deserialize ODS data for customer {}", customerId, e);
            return null;
        }
    }

}
