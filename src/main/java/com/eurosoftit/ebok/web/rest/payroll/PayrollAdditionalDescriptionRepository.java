package com.eurosoftit.ebok.web.rest.payroll;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.Repository;

public interface PayrollAdditionalDescriptionRepository extends Repository<PayrollAdditionalDescription, Long>, JpaSpecificationExecutor<PayrollAdditionalDescription> {
}
