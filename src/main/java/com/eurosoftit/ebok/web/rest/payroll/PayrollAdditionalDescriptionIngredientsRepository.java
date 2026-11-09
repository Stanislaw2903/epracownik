package com.eurosoftit.ebok.web.rest.payroll;

import com.eurosoftit.ebok.domain.DictionaryDTO;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface PayrollAdditionalDescriptionIngredientsRepository extends Repository<PayrollAdditionalDescriptionIngredients, Long>, JpaSpecificationExecutor<PayrollAdditionalDescriptionIngredients> {
    @Query("SELECT new com.eurosoftit.ebok.domain.DictionaryDTO(i.code, i.name) FROM Ingredients i")
    List<DictionaryDTO> getIngredients();
}
