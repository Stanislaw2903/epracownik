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
@Subselect(" SELECT row_number() over() as id, CONCAT(pp.prz_wkp_serial,date_part('year', date_trunc('MONTH',p.lpl_data_wyplaty)) " +
  " ,date_part('MONTH', date_trunc('MONTH',p.lpl_data_wyplaty))) AS AUXILIARY_ID, " +
  "  p.lpl_oso_serial as external_id,  " +
  "  date_trunc('MONTH',p.lpl_data_wyplaty) as payment_date,  " +
  "  pp.prz_wkp_serial as code,  " +
  "  sum(pp.prz_kwota) as price,  " +
  "  pp.prz_dod_1 AS TYPE  " +
  "  FROM PLACE_PRZ pp  " +
  "  JOIN PLACE p ON p.lpl_serial = pp.prz_lpl_serial " +
  "  JOIN LISTY l ON l.lis_serial = p.lpl_lis_serial " +
  "  WHERE l.lis_char_1 = 'T'  and pp.prz_lis_serial = l.lis_serial and pp.prz_oso_serial = p.lpl_oso_serial " +
  "  GROUP BY pp.prz_dod_1,p.lpl_oso_serial, date_trunc('MONTH',p.lpl_data_wyplaty), pp.prz_wkp_serial")
@Immutable
@Getter
@Setter
public class PayrollAdditionalDescriptionBreaks {
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
  @Column(name = "type")
  private String type;
}
