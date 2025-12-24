package com.motaz.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {

    private Long customerId;
    private String iban;
    private AccountType accountType;
    private String currency;
}
