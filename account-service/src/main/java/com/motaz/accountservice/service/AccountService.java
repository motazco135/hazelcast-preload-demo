package com.motaz.accountservice.service;

import com.motaz.accountservice.dto.AccountDto;
import com.motaz.accountservice.dto.AccountStatus;
import com.motaz.accountservice.dto.CreateAccountRequest;
import com.motaz.accountservice.mapper.AccountMapper;
import com.motaz.accountservice.model.AccountEntity;
import com.motaz.accountservice.repo.AccountEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountEntityRepository accountEntityRepository;
    private final AccountMapper accountMapper;
    private final AccountEventProducer accountEventProducer;

    /**
     * Find all accounts for a customer by customer ID
     */
    @Transactional(readOnly = true)
    public Page<AccountDto> findAccountsByCustomerId(Long customerId, Pageable pageable) {
        log.info("Finding accounts for customer ID: {}", customerId);
        Page<AccountEntity> entities = accountEntityRepository.findByCustomerId(customerId, pageable);
        return entities.map(accountMapper::toResponse);
    }

    /**
     * Create a new account for a customer
     */
    @Transactional
    public AccountDto createAccountForCustomer(CreateAccountRequest request, boolean isPrimary) {
        log.info("Creating account for customer ID: {}", request.getCustomerId());

        AccountEntity accountEntity = accountMapper.toEntity(request);
        accountEntity.setAccountStatus(AccountStatus.ACTIVE);
        accountEntity.setAvailableBalance(BigDecimal.ZERO);
        accountEntity.setLedgerBalance(BigDecimal.ZERO);
        accountEntity.setCurrency("SAR");
        accountEntity.setIsPrimary(isPrimary);

        AccountEntity savedAccount = accountEntityRepository.save(accountEntity);
        log.info("Account created successfully with ID: {} for customer ID: {}",
                savedAccount.getAccountId(), savedAccount.getCustomerId());
        AccountDto response = accountMapper.toResponse(savedAccount);
        accountEventProducer.publishAccount(response);
        return response;
    }

    /**
     * Check if a customer account exists
     */
    public boolean checkCustomerAccountExists(Long customerId) {
        log.info("Checking if account exists for customer ID: {}", customerId);
        return accountEntityRepository.existsByCustomerId(customerId);
    }

    /**
     * Update account status by IBAN and customer ID
     */
    @Transactional
    public AccountDto updateAccountStatus(String iban, Long customerId, AccountStatus newStatus) {
        log.info("Updating account status for IBAN: {} and customer ID: {} to status: {}", iban, customerId, newStatus);

        AccountEntity account = accountEntityRepository.findByIbanAndCustomerId(iban, customerId)
                .orElseThrow(() -> new RuntimeException("Account not found with IBAN: " + iban + " and customer ID: " + customerId));

        account.setAccountStatus(newStatus);
        AccountEntity updatedAccount = accountEntityRepository.save(account);

        log.info("Account status updated successfully for account ID: {}", updatedAccount.getAccountId());
        AccountDto response =  accountMapper.toResponse(updatedAccount);


        accountEventProducer.publishAccount(response);

        return response;
    }

    /**
     * Top up account balance
     */
    @Transactional
    public AccountDto topUpAccount(String iban, Long customerId, BigDecimal amount) {
        log.info("Topping up account with IBAN: {} and customer ID: {} with amount: {}", iban, customerId, amount);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Top-up amount must be greater than zero");
        }

        AccountEntity account = accountEntityRepository.findByIbanAndCustomerId(iban, customerId)
                .orElseThrow(() -> new RuntimeException("Account not found with IBAN: " + iban + " and customer ID: " + customerId));

        BigDecimal newAvailableBalance = account.getAvailableBalance().add(amount);
        BigDecimal newLedgerBalance = account.getLedgerBalance().add(amount);

        account.setAvailableBalance(newAvailableBalance);
        account.setLedgerBalance(newLedgerBalance);

        AccountEntity updatedAccount = accountEntityRepository.save(account);

        log.info("Account topped up successfully. Account ID: {}, New Available Balance: {}, New Ledger Balance: {}",
                updatedAccount.getAccountId(), newAvailableBalance, newLedgerBalance);

        AccountDto response =  accountMapper.toResponse(updatedAccount);

        accountEventProducer.publishAccount(response);

        return response;
    }

}
