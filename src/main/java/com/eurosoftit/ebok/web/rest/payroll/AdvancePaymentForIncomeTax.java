package com.eurosoftit.ebok.web.rest.payroll;

import java.math.BigDecimal;

public class AdvancePaymentForIncomeTax {
    private BigDecimal due;
    private BigDecimal collected;
    private BigDecimal deductions;
    private BigDecimal toPay;

    public AdvancePaymentForIncomeTax(BigDecimal due, BigDecimal collected, BigDecimal deductions, BigDecimal toPay) {
        this.due = due;
        this.collected = collected;
        this.deductions = deductions;
        this.toPay = toPay;
    }

    public BigDecimal getDue() {
        return due;
    }

    public BigDecimal getCollected() {
        return collected;
    }

    public BigDecimal getDeductions() {
        return deductions;
    }

    public BigDecimal getToPay() {
        return toPay;
    }
}
