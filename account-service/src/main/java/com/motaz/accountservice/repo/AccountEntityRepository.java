package com.motaz.accountservice.repo;

import com.motaz.accountservice.model.AccountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountEntityRepository extends JpaRepository<AccountEntity, Long> {
    List<AccountEntity> findByCustomerId(Long customerId);
    Page<AccountEntity> findByCustomerId(Long customerId, Pageable pageable);
    boolean existsByCustomerId(Long customerId);
    Optional<AccountEntity> findByIbanAndCustomerId(String iban, Long customerId);
}
