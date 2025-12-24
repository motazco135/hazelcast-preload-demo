package com.motaz.accountservice.model;

import com.motaz.accountservice.converter.AccountStatusConverter;
import com.motaz.accountservice.converter.AccountTypeConverter;
import com.motaz.accountservice.dto.AccountStatus;
import com.motaz.accountservice.dto.AccountType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@Table(name="t_account")
public class AccountEntity {
    @Id
    @Column(name = "account_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "iban", nullable = false)
    private String iban;

    @Convert(converter = AccountTypeConverter.class)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Convert(converter = AccountStatusConverter.class)
    @Column(name = "account_status", nullable = false)
    private AccountStatus accountStatus;

    @ColumnDefault("0")
    @Column(name = "available_balance",  precision = 18, scale = 2)
    private BigDecimal availableBalance;

    @ColumnDefault("0")
    @Column(name = "ledger_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal ledgerBalance;

    @ColumnDefault("false")
    @Column(name = "is_primary")
    private Boolean isPrimary;

    @Column(name = "opened_at", nullable = false)
    private Instant openedAt;

    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ColumnDefault("now()")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @PreUpdate
    private void setUpdatedAt() {
        this.updatedAt = Instant.now();
    }

    @PrePersist
    private void setCreatedAt() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.openedAt = Instant.now();
    }

}
