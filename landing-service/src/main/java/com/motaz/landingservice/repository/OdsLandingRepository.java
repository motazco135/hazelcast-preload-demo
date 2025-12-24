package com.motaz.landingservice.repository;

import com.motaz.landingservice.model.OdsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OdsLandingRepository extends JpaRepository<OdsEntity, Long> {
}
