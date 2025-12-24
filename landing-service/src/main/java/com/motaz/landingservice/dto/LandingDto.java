package com.motaz.landingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LandingDto implements Serializable {

    private Long customerId;
    private List<AccountDto> accounts;
    private CustomerProfileDto customerProfileDto;
    private Instant lastUpdatedAt;



}
