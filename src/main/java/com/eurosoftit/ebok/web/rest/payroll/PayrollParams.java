package com.eurosoftit.ebok.web.rest.payroll;

import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.ZonedDateTimeFilter;

import java.io.Serializable;
import java.util.Objects;

public class PayrollParams implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;
    private ZonedDateTimeFilter paymentDate;

    public PayrollParams() {
    }

    public PayrollParams(PayrollParams other) {
        this.paymentDate = other.paymentDate == null ? null : other.paymentDate.copy();
    }

    @Override
    public Criteria copy() {
        return new PayrollParams(this);
    }

    public ZonedDateTimeFilter getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(ZonedDateTimeFilter paymentDate) {
        this.paymentDate = paymentDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PayrollParams that = (PayrollParams) o;
        return Objects.equals(paymentDate, that.paymentDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentDate);
    }
}
