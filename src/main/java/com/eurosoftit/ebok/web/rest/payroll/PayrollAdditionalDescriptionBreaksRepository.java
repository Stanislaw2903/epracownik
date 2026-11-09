package com.eurosoftit.ebok.web.rest.payroll;

import com.eurosoftit.ebok.domain.DictionaryDTO;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface PayrollAdditionalDescriptionBreaksRepository extends Repository<PayrollAdditionalDescriptionBreaks, Long>, JpaSpecificationExecutor<PayrollAdditionalDescriptionBreaks> {
    @Query("SELECT new com.eurosoftit.ebok.domain.DictionaryDTO(b.code, b.name) FROM Breaks b")
    List<DictionaryDTO> getBreaks();
}
