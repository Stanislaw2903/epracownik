package com.eurosoftit.ebok.web.rest.payroll;

import com.eurosoftit.ebok.domain.DictionaryDTO;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.List;

@Api
@RestController
@RequestMapping("/api/payroll")
public class PayrollResource {
    private final Logger log = LoggerFactory.getLogger(PayrollResource.class);

    @Autowired
    private PayrollService payrollService;

    @GetMapping
    @Transactional
    List<PayrollDTO> getPayroll(@RequestParam("year") Integer year){
        log.debug("REST request to get payroll");
        return payrollService.getPayroll(year);
    }

    @GetMapping("/details")
    @Transactional
    PayrollDetailsDTO getPayrollDetails(@RequestParam("year") Integer year){
        log.debug("REST request to get payroll details");
        return payrollService.getPayrollDetails(year);
    }

    @GetMapping("/additionalDescription")
    @Transactional
    PayrollAdditionalDescriptionDTO getPayrollAdditionalDescription(@RequestParam("year") Integer year, @RequestParam("month") Integer month) {
        log.debug("REST request to get payroll additional description");
        return payrollService.getPayrollAdditionalDescription(year, month);
    }

    @GetMapping("/ingredients")
    @Transactional
    List<DictionaryDTO> getIngredients() {
        return payrollService.getIngredients();
    }

    @GetMapping("/breaks")
    @Transactional
    List<DictionaryDTO> getBreaks() {
        return payrollService.getBreaks();
    }

    @GetMapping("/deductions")
    @Transactional
    List<DictionaryDTO> getDeductions() {
        return payrollService.getDeductions();
    }
}
