package com.motaz.landingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto implements Serializable {

    private Long accountId;
    private Long customerId;
    private String iban;
    private AccountType accountType;
    private String currency;
    private AccountStatus accountStatus;
    private BigDecimal availableBalance;
    private BigDecimal ledgerBalance;
    private Boolean isPrimary;
    private Instant openedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
