package com.eurosoftit.ebok.web.rest.payroll;

import java.math.BigDecimal;

public class HealthInsurancePremium {
    private BigDecimal taken;
    private BigDecimal deducted;

    public HealthInsurancePremium(BigDecimal taken, BigDecimal deducted) {
        this.taken = taken;
        this.deducted = deducted;
    }

    public BigDecimal getTaken() {
        return taken;
    }

    public BigDecimal getDeducted() {
        return deducted;
    }
}
