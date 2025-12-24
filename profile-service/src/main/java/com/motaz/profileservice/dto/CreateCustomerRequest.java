package com.motaz.profileservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String address;
    private IDType idType;
    private String idNumber;
}
