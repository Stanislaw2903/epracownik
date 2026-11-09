package com.eurosoftit.ebok.web.rest.dashboard;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Subselect("SELECT ds.dst_oso_serial as external_id, sum(COALESCE(ds.dst_int_4,0)) as vested , \n" +
    "sum(COALESCE(ds.dst_numeric_3,0)) as shift_vested, dst_int_1 as _year\n" +
    "from dane_stat ds where dst_oso_serial is not null and dst_int_1 is not null \n" +
    "group by dst_oso_serial, dst_int_1")
@Immutable
public class VestedVacationView {
    @Id
    @Column(name = "external_id")
    private Long externalId;
    @Column(name = "_year")
    private Long year;
    @Column(name = "vested")
    private BigDecimal vested;
    @Column(name = "shift_vested")
    private BigDecimal shiftVested;
}
