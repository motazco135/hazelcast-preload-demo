package com.motaz.landingservice.service;

import com.motaz.landingservice.dto.AccountDto;
import com.motaz.landingservice.dto.AccountPageResponse;
import com.motaz.landingservice.dto.CustomerProfileDto;
import com.motaz.landingservice.dto.LandingDto;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalApiClient {

    @Value("${external-services.account-service}")
    private String accountServiceUrl;

    @Value("${external-services.profile-service}")
    private String profileServiceUrl;

    private final RestClient restClient;
    private final CacheService cacheService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    /**
     * Fetch customer data from both services in parallel and cache in Hazelcast
     */
    public LandingDto fetchAndCacheCustomerData(Long customerId) {
        long startTime = System.currentTimeMillis();
        log.info("Starting parallel fetch from REST services for customer ID: {}", customerId);

        // Fetch customer profile and accounts in parallel
        CompletableFuture<CustomerProfileDto> profileFuture = CompletableFuture.supplyAsync(
                () -> fetchCustomerProfile(customerId), executorService);

        CompletableFuture<List<AccountDto>> accountsFuture = CompletableFuture.supplyAsync(
                () -> fetchCustomerAccounts(customerId), executorService);

        // Wait for both to complete
        CompletableFuture.allOf(profileFuture, accountsFuture).join();

        // Get results
        CustomerProfileDto customerProfile = profileFuture.join();
        List<AccountDto> accounts = accountsFuture.join();

        long fetchTime = System.currentTimeMillis() - startTime;
        log.info("Completed parallel fetch from REST services for customer ID: {} in {} ms", customerId, fetchTime);

        // Create and populate landingDto
        LandingDto landingDto = new LandingDto();
        landingDto.setCustomerId(customerId);
        landingDto.setCustomerProfileDto(customerProfile);
        landingDto.setAccounts(accounts);
        landingDto.setLastUpdatedAt(Instant.now());

        // Push to ods and cache
        long cacheStartTime = System.currentTimeMillis();
        cacheService.saveToHazelcastAndDb(customerId, landingDto);
        long cacheTime = System.currentTimeMillis() - cacheStartTime;
        log.info("Cached customer data in Hazelcast for customer ID: {} in {} ms", customerId, cacheTime);

        long totalTime = System.currentTimeMillis() - startTime;
        log.info("Total time for fetch and cache for customer ID: {} is {} ms", customerId, totalTime);

        return landingDto;
    }

    /**
     * Fetch customer profile from profile-service
     */
    private CustomerProfileDto fetchCustomerProfile(Long customerId) {
        try {
            log.info("Fetching customer profile for customer ID: {}", customerId);
            CustomerProfileDto customerProfile = restClient.get()
                    .uri(profileServiceUrl +"/api/customers/" + customerId)
                    .retrieve()
                    .body(CustomerProfileDto.class);
            log.info("Successfully fetched customer profile for customer ID: {}", customerId);
            return customerProfile;
        } catch (Exception e) {
            log.error("Failed to fetch customer profile for customer ID: {}", customerId, e);
            return null;
        }
    }

    /**
     * Fetch all accounts from account-service
     */
    private List<AccountDto> fetchCustomerAccounts(Long customerId) {
        try {
            log.info("Fetching accounts for customer ID: {}", customerId);
            AccountPageResponse accountsPage = restClient.get()
                    .uri(accountServiceUrl+"/api/v1/accounts/customer/" + customerId +
                            "?page=0&size=2000&sort=customerId,ASC&forceFirstAndLastRels=true")
                    .retrieve()
                    .body(AccountPageResponse.class);

            if (accountsPage != null && accountsPage.get_embedded() != null
                    && accountsPage.get_embedded().getAccountDtoList() != null) {
                List<AccountDto> accounts = accountsPage.get_embedded().getAccountDtoList();
                log.info("Successfully fetched {} accounts for customer ID: {}", accounts.size(), customerId);
                return accounts;
            } else {
                log.warn("No accounts found for customer ID: {}", customerId);
                return new ArrayList<>();
            }
        } catch (Exception e) {
            log.error("Failed to fetch accounts for customer ID: {}", customerId, e);
            return new ArrayList<>();
        }
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
