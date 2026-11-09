package com.eurosoftit.ebok.repository;

import com.eurosoftit.ebok.domain.Qualifications;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Qualifications entity.
 */
@SuppressWarnings("unused")
@Repository
public interface QualificationsRepository extends JpaRepository<Qualifications, Long>, JpaSpecificationExecutor<Qualifications> {
}
