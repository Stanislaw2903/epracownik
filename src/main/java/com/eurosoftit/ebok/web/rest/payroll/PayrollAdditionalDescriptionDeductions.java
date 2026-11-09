package com.eurosoftit.ebok.web.rest.payroll;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Subselect("SELECT row_number() over() as id, " +
    "p.lpl_oso_serial as external_id, " +
    "date_trunc('MONTH',p.lpl_data_wyplaty) as payment_date, " +
    "pp.ppo_wpo_serial as code, " +
    "sum(pp.ppo_kwota) as price " +
    "FROM PLACE_POT pp " +
    "JOIN PLACE p ON pp.ppo_lis_serial = p.lpl_lis_serial and pp.ppo_oso_serial = p.lpl_oso_serial " +
    "JOIN LISTY l ON l.lis_serial = p.lpl_lis_serial " +
    "WHERE l.lis_char_1 = 'T' " +
    "GROUP BY p.lpl_oso_serial, date_trunc('MONTH',p.lpl_data_wyplaty), pp.ppo_wpo_serial")
@Immutable
@Getter
@Setter
public class PayrollAdditionalDescriptionDeductions {
    @Id
    @JsonIgnore
    @Column(name = "id")
    private Long id;
    @JsonIgnore
    @Column(name = "payment_date")
    private ZonedDateTime paymentDate;
    @Column(name = "price")
    private BigDecimal price;
    @JsonIgnore
    @Column(name = "external_id")
    private Long externalId;
    @Column(name = "code")
    private Long code;
}
