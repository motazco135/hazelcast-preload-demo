package com.motaz.accountservice.controller;

import com.motaz.accountservice.dto.AccountDto;
import com.motaz.accountservice.dto.AccountStatus;
import com.motaz.accountservice.dto.CreateAccountRequest;
import com.motaz.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * Get all accounts for a customer by customer ID with pagination
     */
    @GetMapping("/customer/{customerId}")
    public PagedModel<EntityModel<AccountDto>> getAccountsByCustomerId(
            @PathVariable Long customerId,
            @ParameterObject
            @PageableDefault(size = 20, sort = "customerId") Pageable pageable,
            PagedResourcesAssembler<AccountDto> assembler ) {
        log.info("GET request to fetch accounts for customer ID: {}", customerId);
        Page<AccountDto> accounts = accountService.findAccountsByCustomerId(customerId, pageable);
        return  assembler.toModel(accounts);
    }

    /**
     * Create a new account for a customer
     */
    @PostMapping
    public ResponseEntity<AccountDto> createAccount(
            @RequestBody CreateAccountRequest request,
            @RequestParam(defaultValue = "false") boolean isPrimary) {
        log.info("POST request to create account for customer ID: {}", request.getCustomerId());
        AccountDto createdAccount = accountService.createAccountForCustomer(request, isPrimary);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

    /**
     * Check if customer account exists
     */
    @GetMapping("/customer/{customerId}/exists")
    public ResponseEntity<Boolean> checkAccountExists(@PathVariable Long customerId) {
        log.info("GET request to check if account exists for customer ID: {}", customerId);
        boolean exists = accountService.checkCustomerAccountExists(customerId);
        return ResponseEntity.ok(exists);
    }

    /**
     * Update account status by IBAN and customer ID
     */
    @PatchMapping("/status")
    public ResponseEntity<AccountDto> updateAccountStatus(
            @RequestParam String iban,
            @RequestParam Long customerId,
            @RequestParam AccountStatus status) {
        log.info("PATCH request to update account status for IBAN: {} and customer ID: {}", iban, customerId);
        AccountDto updatedAccount = accountService.updateAccountStatus(iban, customerId, status);
        return ResponseEntity.ok(updatedAccount);
    }

    /**
     * Top up account balance
     */
    @PatchMapping("/topup")
    public ResponseEntity<AccountDto> topUpAccount(
            @RequestParam String iban,
            @RequestParam Long customerId,
            @RequestParam java.math.BigDecimal amount) {
        log.info("PATCH request to top up account for IBAN: {} and customer ID: {} with amount: {}", iban, customerId, amount);
        AccountDto updatedAccount = accountService.topUpAccount(iban, customerId, amount);
        return ResponseEntity.ok(updatedAccount);
    }
}
