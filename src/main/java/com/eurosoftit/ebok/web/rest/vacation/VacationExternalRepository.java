package com.eurosoftit.ebok.web.rest.vacation;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;

public interface VacationExternalRepository extends Repository<VacationExternal, Long>, JpaSpecificationExecutor<Vacation> {
    @Query("select v from VacationExternal v where v.externalId = :externalId and v.dateFrom >= :startYear and v.dateTo <= :endYear")
    List<VacationExternal> getHistoryByYearForUser(@Param("startYear") ZonedDateTime startYear, @Param("endYear") ZonedDateTime endYear, @Param("externalId") Long externalId);
}
