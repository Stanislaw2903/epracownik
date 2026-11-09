package com.eurosoftit.ebok.web.rest.payroll;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PayrollDTO implements Serializable {
    @JsonIgnore
    private Long id;
    private ZonedDateTime paymentDate;
    private Long externalId;
    private ZonedDateTime dateTo;
    private ZonedDateTime dateFrom;
    private BigDecimal salaryInCash;
    private BigDecimal salaryInKind;
    private BigDecimal taxed;
    private BigDecimal grossPremium;
    private BigDecimal taxedWithoutZus;
    private BigDecimal withoutZusAndTax;
    private BigDecimal socialInsuranceBenefits;
    private BigDecimal inability2;

    private SocialSecurityContributionsPaid socialSecurityContributionsPaid;

    private BigDecimal costOfGettingIncome;
    private BigDecimal socialSecurityDeducted;
    private BigDecimal incomeInAGivenMonth;
    private BigDecimal incomeFromBeginningOfYear;
    private BigDecimal rate;
    private BigDecimal freeAmount;
    private BigDecimal accruedAdvancePaymentForIncomeTax;

    private HealthInsurancePremium healthInsurancePremium;

    private AdvancePaymentForIncomeTax advancePaymentForIncomeTax;
}
