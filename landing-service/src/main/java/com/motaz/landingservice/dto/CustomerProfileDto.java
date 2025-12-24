package com.motaz.landingservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProfileDto implements Serializable {
    private static final long serialVersionUID = 1L;
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
