package com.eurosoftit.ebok.web.rest.payroll;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Subselect("SELECT distinct WPO_SERIAL as CODE, WPO_OPIS as NAME FROM WYN_POTRACENIA")
@Immutable
@Getter
@Setter
public class Deductions {
    @Id
    @Column(name = "code")
    private Long code;
    @Column(name = "name")
    private String name;
}
