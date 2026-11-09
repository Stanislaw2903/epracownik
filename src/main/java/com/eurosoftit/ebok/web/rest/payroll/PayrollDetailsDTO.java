package com.eurosoftit.ebok.web.rest.payroll;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class PayrollDetailsDTO implements Serializable {
    private List<PayrollDetails> rows;
    private List<PayrollDetailsListColumns> columns;
}
