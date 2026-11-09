package com.eurosoftit.ebok.web.rest.management;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

interface ManagementRepository extends JpaRepository<ManagementEntity, Long>, JpaSpecificationExecutor<ManagementEntity> {

    @Query("SELECT m.value FROM ManagementEntity m WHERE m.type = :managementType")
    Optional<String> findValueByType(@Param("managementType") ManagementType managementType);

    @Query("SELECT m FROM ManagementEntity m WHERE m.type = :managementType")
    Optional<ManagementEntity> findByType(@Param("managementType") ManagementType managementType);
}
