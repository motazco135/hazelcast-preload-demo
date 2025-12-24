package com.motaz.landingservice.service;

import com.motaz.landingservice.dto.AccountDto;
import com.motaz.landingservice.dto.CustomerProfileDto;
import com.motaz.landingservice.dto.LandingDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProcessor {

    private final CacheService cacheService;

    public void upsertCustomerProfile(CustomerProfileDto customerProfileDto) {
        cacheService.upsertCustomerProfile(customerProfileDto.getCustomerId(), customerProfileDto);
    }

    public void upsertAccount(AccountDto accountDto) {
        cacheService.upsertAccount(accountDto.getCustomerId(), accountDto);
    }
}
