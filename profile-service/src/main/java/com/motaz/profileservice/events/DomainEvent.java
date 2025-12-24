package com.motaz.profileservice.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class DomainEvent {
    private String eventId;
    private String eventType;
    private Instant timestamp;
    private String correlationId;
    private String causationId;
    private Integer version;
}
