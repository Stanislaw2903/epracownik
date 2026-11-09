package com.eurosoftit.ebok.web.rest.payroll;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SocialSecurityContributionsPaid implements Serializable {
    private BigDecimal retirement;
    private BigDecimal disability;
    private BigDecimal sickness;
    private BigDecimal contributionsSum;
}
