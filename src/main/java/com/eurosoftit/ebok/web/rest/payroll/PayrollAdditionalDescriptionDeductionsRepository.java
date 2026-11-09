package com.eurosoftit.ebok.web.rest.payroll;

import com.eurosoftit.ebok.domain.DictionaryDTO;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface PayrollAdditionalDescriptionDeductionsRepository extends Repository<PayrollAdditionalDescriptionDeductions, Long>, JpaSpecificationExecutor<PayrollAdditionalDescriptionDeductions> {
    @Query("SELECT new com.eurosoftit.ebok.domain.DictionaryDTO(d.code, d.name) FROM Deductions d")
    List<DictionaryDTO> getDeductions();
}
