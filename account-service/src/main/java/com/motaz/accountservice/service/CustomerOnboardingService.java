package com.motaz.accountservice.service;

import com.motaz.accountservice.dto.AccountType;
import com.motaz.accountservice.dto.CreateAccountRequest;
import com.motaz.accountservice.dto.CustomerProfileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerOnboardingService {

    private final AccountService accountService;

    public void handleCustomerProfile(CustomerProfileDto customerProfileDto) {
        String numericString = IntStream.range(0, 10)
                .mapToObj(i -> String.valueOf(ThreadLocalRandom.current().nextInt(10)))
                .collect(Collectors.joining());
        Iban ibanBuilder = new Iban.Builder()
                .countryCode(CountryCode.SA)
                .bankCode("5545000000")
                .accountNumber(numericString)
                .build();
       //Check if a customer exists
        if( accountService.checkCustomerAccountExists(customerProfileDto.getCustomerId())){
            CreateAccountRequest createAccountRequest = new CreateAccountRequest();
            createAccountRequest.setCustomerId(customerProfileDto.getCustomerId());
            createAccountRequest.setAccountType(AccountType.SAVINGS);
            createAccountRequest.setIban(ibanBuilder.getBban());
            accountService.createAccountForCustomer(createAccountRequest, false);
        }else{
            CreateAccountRequest createAccountRequest = new CreateAccountRequest();
            createAccountRequest.setCustomerId(customerProfileDto.getCustomerId());
            createAccountRequest.setAccountType(AccountType.CURRENT);
            createAccountRequest.setIban(ibanBuilder.getBban());
            accountService.createAccountForCustomer(createAccountRequest, true);
        }

    }

}
