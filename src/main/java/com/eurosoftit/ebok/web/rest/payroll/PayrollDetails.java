package com.eurosoftit.ebok.web.rest.payroll;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Subselect("SELECT row_number() over() as id,\n" +
    "ps.skl_oso_serial as external_id,\n" +
    "wks.wks_serial as code,\n" +
    "date_trunc('MONTH',p.lpl_data_wyplaty) as payment_date,\n" +
    "sum(ps.skl_kwota) as amount\n" +
    "FROM PLACE_SKL ps " +
    "JOIN wyn_kod_skl wks ON wks.wks_serial = ps.skl_wks_serial " +
    "JOIN PLACE p ON p.lpl_serial = ps.skl_lpl_serial \n" +
    "JOIN LISTY l ON l.lis_serial = p.lpl_lis_serial " +
    "WHERE l.lis_char_1 = 'T' " +
    "GROUP BY ps.skl_oso_serial, date_trunc('MONTH',p.lpl_data_wyplaty), wks.wks_serial\n" +
    "ORDER BY ps.skl_oso_serial, date_trunc('MONTH',p.lpl_data_wyplaty)\n")
@Immutable
@Getter
@Setter
public class PayrollDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @JsonIgnore
    @Column(name = "id", insertable = false, updatable = false)
    private Long id;

    @Column(name = "payment_date", insertable = false, updatable = false)
    private ZonedDateTime paymentDate;

    @JsonIgnore
    @Column(name = "external_id", insertable = false, updatable = false)
    private Long externalId;

    @Column(name = "code", insertable = false, updatable = false)
    private String code;

    @Column(name = "amount", insertable = false, updatable = false)
    private BigDecimal amount;
}
