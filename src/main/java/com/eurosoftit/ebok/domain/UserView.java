package com.eurosoftit.ebok.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Subselect;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Subselect("select " +
    " ju.id as ID," +
    " o.oso_pesel as PESEL," +
    " ju.email as EMAIL," +
    " o.oso_urodz_data AS BIRTH_DATE," +
    " EXTRACT( DAY FROM o.oso_urodz_data ) AS BIRTH_DAY," +
    " EXTRACT( MONTH FROM o.oso_urodz_data ) AS BIRTH_MONTH," +
    " ju.phone_number AS PHONE," +
    " ju.ACTIVATED AS ACTIVATED" +
    " from jhi_user ju " +
    " JOIN osoba o ON ju.external_id = o.oso_serial")
public class UserView {
    @Id
    private Long id;
    private String pesel;
    private LocalDate birthDate;
    private String email;
    private Integer birthDay;
    private Integer birthMonth;

    private String phone;
    private boolean activated;
}
