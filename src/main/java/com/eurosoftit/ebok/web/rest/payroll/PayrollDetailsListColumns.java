package com.eurosoftit.ebok.web.rest.payroll;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Subselect("SELECT ps.skl_oso_serial as external_id,\n" +
    "wks.wks_opis as description,\n" +
    "wks.wks_serial as code,\n" +
    "extract('YEAR' from p.lpl_data_wyplaty) as current_year " +
    "FROM PLACE_SKL ps JOIN wyn_kod_skl wks ON wks.wks_serial = ps.skl_wks_serial " +
    "JOIN PLACE p ON p.lpl_serial = ps.skl_lpl_serial " +
    "JOIN LISTY l ON l.lis_serial = p.lpl_lis_serial " +
    "WHERE l.lis_char_1 = 'T' " +
    "GROUP BY wks.wks_opis, wks.wks_serial, ps.skl_oso_serial, extract('YEAR' from p.lpl_data_wyplaty)")

@Immutable
@EqualsAndHashCode(of = {"externalId", "code", "description", "currentYear"})
public class PayrollDetailsListColumns implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonIgnore
    @Column(name = "external_id")
    private Long externalId;

    @Id
    @Column(name = "current_year")
    private Integer currentYear;

    @Id
    @Column(name = "code")
    private Long code;

    @Column(name = "description")
    private String description;
}
