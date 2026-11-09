package com.eurosoftit.ebok.web.rest.payroll;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Subselect("SELECT distinct WKS_SERIAL as CODE, WKS_OPIS_SKR as NAME FROM WYN_KOD_SKL")
@Immutable
@Getter
@Setter
public class Ingredients {
    @Id
    @Column(name = "code")
    private Long code;
    @Column(name = "name")
    private String name;
}
