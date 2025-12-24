package com.motaz.landingservice.controller;

import com.motaz.landingservice.dto.LandingDto;
import com.motaz.landingservice.service.LandingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/landing")
public class LandingController {

    private final LandingService landingService;

    // cache miss
    @GetMapping("/customer/{customerId}/cache-miss")
    public ResponseEntity<LandingDto> getCustomerLanding(@PathVariable Long customerId) {
        log.info("GET request for customer landing data, customer ID: {}", customerId);
        return landingService.getCustomerCacheMiss(customerId).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    @GetMapping("/customer/{customerId}/pre-load")
    public ResponseEntity<LandingDto> preLoadCustomer(@PathVariable Long customerId) {
        log.info("Get request for pre-loading customer landing data, customer ID: {}", customerId);
        return landingService.preLoadCustomer(customerId).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}/fan-out")
    public ResponseEntity<LandingDto> fanOutCustomer(@PathVariable Long customerId) {
        log.info("Get request for fan-out customer landing data, customer ID: {}", customerId);
        LandingDto landingDto = landingService.getCustomerFanOut(customerId);
        return ResponseEntity.ok(landingDto);
    }

    @GetMapping("/customer/{customerId}/cache-only")
    public ResponseEntity<LandingDto> getCustomerFromCacheOnly(@PathVariable Long customerId) {
        log.info("Get request for getCustomerFromCacheOnly customer landing data, customer ID: {}", customerId);
        return landingService.getCustomerFromCacheOnly(customerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}/ods-only")
    public ResponseEntity<LandingDto> getCustomerFromOdsOnly(@PathVariable Long customerId) {
        log.info("Get request for getCustomerFromOdsOnly customer landing data, customer ID: {}", customerId);
        return landingService.getCustomerFromOdsOnly(customerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
