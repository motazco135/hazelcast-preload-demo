package com.motaz.profileservice.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class CustomerProfileUpdatedEvent extends DomainEvent {
    private Long customerId;
    private Map<String, Object> changes; // Field-level changes
    private Instant updatedAt;
}
