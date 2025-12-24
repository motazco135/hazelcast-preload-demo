package com.motaz.profileservice.events;

import com.motaz.profileservice.dto.IDType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class CustomerProfileCreatedEvent extends DomainEvent {
    private Long customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private IDType idType;
    private String idNumber;
    private Instant createdAt;
}
