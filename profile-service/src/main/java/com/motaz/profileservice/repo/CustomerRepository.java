package com.motaz.profileservice.repo;

import com.motaz.profileservice.model.CustomerProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerProfileEntity, Long> {
}
