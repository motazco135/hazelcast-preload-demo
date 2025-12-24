package com.motaz.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerProfileDto {
    private Long customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String address;
    private IDType idType;
    private String idNumber;
    private Instant createdAt;
    private Instant updatedAt;
}
