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
@Subselect(" SELECT row_number() over() as id, a.* FROM(SELECT " +
    " CONCAT(ps.skl_wks_serial,date_part('year', date_trunc('MONTH',p.lpl_data_wyplaty)),date_part('MONTH', date_trunc('MONTH',p.lpl_data_wyplaty))) AS AUXILIARY_ID, " +
    " p.lpl_oso_serial as external_id, " +
    "    date_trunc('MONTH',p.lpl_data_wyplaty) as payment_date, " +
    "    ps.skl_wks_serial as code, " +
    "    sum(ps.skl_kwota) as price, " +
    " ps.skl_dod_1 AS type " +
    "    FROM PLACE_SKL ps " +
    "    JOIN PLACE p ON p.lpl_serial = ps.skl_lpl_serial " +
    "    JOIN LISTY l ON l.lis_serial = p.lpl_lis_serial " +
    "    WHERE ps.skl_dod_1 = 'N' and l.lis_char_1 = 'T'" +
    " GROUP BY ps.skl_dod_1,p.lpl_oso_serial, date_trunc('MONTH',p.lpl_data_wyplaty), ps.skl_wks_serial " +
    " UNION ALL " +
    " SELECT " +
    " CONCAT(ps.skl_wks_serial,date_part('year', date_trunc('MONTH',p.lpl_data_wyplaty)),date_part('MONTH', date_trunc('MONTH',p.lpl_data_wyplaty))) AS AUXILIARY_ID," +
    "p.lpl_oso_serial as external_id, " +
    "    date_trunc('MONTH',p.lpl_data_wyplaty) as payment_date, " +
    "    ps.skl_wks_serial as code, " +
    "    sum(ps.skl_kwota) as price, " +
    " ps.skl_dod_1 AS TYPE " +
    "    FROM PLACE_SKL ps " +
    "    JOIN PLACE p ON p.lpl_serial = ps.skl_lpl_serial " +
    "    JOIN LISTY l ON l.lis_serial = p.lpl_lis_serial " +
    " WHERE ps.skl_dod_1 = 'T' and l.lis_char_1 = 'T' " +
    " GROUP BY ps.skl_dod_1,p.lpl_oso_serial, date_trunc('MONTH',p.lpl_data_wyplaty), ps.skl_wks_serial " +
    " UNION ALL " +
    " SELECT " +
    "CONCAT(ps.skl_wks_serial,date_part('year', date_trunc('MONTH',p.lpl_data_wyplaty)),date_part('MONTH', date_trunc('MONTH',p.lpl_data_wyplaty))) AS AUXILIARY_ID, " +
    "p.lpl_oso_serial as external_id, " +
    "    date_trunc('MONTH',p.lpl_data_wyplaty) as payment_date, " +
    "    ps.skl_wks_serial as code, " +
    "    sum(ps.skl_kwota) as price, " +
    "ps.skl_dod_1 AS TYPE " +
    "    FROM PLACE_SKL ps " +
    "    JOIN PLACE p ON p.lpl_serial = ps.skl_lpl_serial  " +
    "    JOIN LISTY l ON l.lis_serial = p.lpl_lis_serial " +
    "WHERE ps.skl_dod_1 = '3' and l.lis_char_1 = 'T' " +
    "GROUP BY ps.skl_dod_1,p.lpl_oso_serial, date_trunc('MONTH',p.lpl_data_wyplaty), ps.skl_wks_serial) a")
@Immutable
@Getter
@Setter
public class PayrollAdditionalDescriptionIngredients {
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
