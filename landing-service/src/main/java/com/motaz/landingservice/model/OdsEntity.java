package com.motaz.landingservice.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Data
@Entity
@Table(name = "t_ods_landing")
public class OdsEntity {

    @Id
    @Column(name = "customer_id")
    private Long customerId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "bundle_json", columnDefinition = "jsonb")
    private String bundleJson;

    @Column(name = "last_updated_at")
    private Instant lastUpdatedAt;

    @PreUpdate
    @PrePersist
    public void setLastUpdatedAt() {
        this.lastUpdatedAt = Instant.now();
    }

}
