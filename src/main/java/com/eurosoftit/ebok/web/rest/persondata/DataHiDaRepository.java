package com.eurosoftit.ebok.web.rest.persondata;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface DataHiDaRepository extends JpaRepository<DataHiDa, Long> {
    @Query("select new com.eurosoftit.ebok.web.rest.persondata.PrivilegesDTO(dhd.name,dhd.completionDate,dhd.dateTo) from DataHiDa dhd where dhd.externalId = :externalId")
    List<PrivilegesDTO> findPrivilegesById(@Param("externalId") Long externalId);

    @Query("select new com.eurosoftit.ebok.web.rest.persondata.CoursesDTO(dhd.name,dhd.completionDate,dhd.donated) from DataHiDa dhd where dhd.externalId = :externalId")
    List<CoursesDTO> findCoursesById(@Param("externalId") Long externalId);

    @Query("select new com.eurosoftit.ebok.web.rest.persondata.PrivilegesDTO(dhd.name,dhd.completionDate,dhd.dateTo) from DataHiDa dhd where dhd.externalId = :externalId")
    Page<PrivilegesDTO> findPrivilegesById(@Param("externalId") Long externalId, Pageable pageable);

    @Query("select new com.eurosoftit.ebok.web.rest.persondata.CoursesDTO(dhd.name,dhd.completionDate,dhd.donated) from DataHiDa dhd where dhd.externalId = :externalId")
    Page<CoursesDTO> findCoursesById(@Param("externalId") Long externalId, Pageable pageable);
}
