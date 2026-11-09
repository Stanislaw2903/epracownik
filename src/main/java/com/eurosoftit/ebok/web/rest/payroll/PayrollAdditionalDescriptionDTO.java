package com.eurosoftit.ebok.web.rest.payroll;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PayrollAdditionalDescriptionDTO implements Serializable {
    PayrollAdditionalDescription content;
    List<PayrollAdditionalDescriptionDeductions> deductions;
    List<PayrollAdditionalDescriptionIngredients> ingredients;
    List<PayrollAdditionalDescriptionBreaks> breaks;
}
