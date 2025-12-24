package com.motaz.profileservice.model;

import com.motaz.profileservice.converter.IDTypeConverter;
import com.motaz.profileservice.dto.IDType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Data
@Entity
@Table(name="t_Customer")
public class CustomerProfileEntity {

    @Id
    @Column(name = "customer_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @Column(name = "first_name",nullable = false)
    private String firstName;

    @Column(name = "last_name",nullable = false)
    private String lastName;

    private String email;
    private String mobile;
    private String address;

    @Convert(converter = IDTypeConverter.class)
    @Column(name = "id_type", nullable = false)
    private IDType idType;

    @Column(name = "id_number", nullable = false)
    private String idNumber;

    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ColumnDefault("now()")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @PreUpdate
    private void setUpdatedAt() {
        this.updatedAt = Instant.now();
    }

    @PrePersist
    private void setCreatedAt() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
}
