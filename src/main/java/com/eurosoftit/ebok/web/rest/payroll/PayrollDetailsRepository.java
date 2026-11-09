package com.eurosoftit.ebok.web.rest.payroll;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PayrollDetailsRepository extends Repository<PayrollDetails, Long>, JpaSpecificationExecutor<PayrollDetails> {
    @Query("select u from PayrollDetailsListColumns u where u.externalId = :externalId and u.currentYear = :currentYear")
    List<PayrollDetailsListColumns> findAllColumns(@Param("externalId") Long externalId, @Param("currentYear") Integer currentYear);
}
