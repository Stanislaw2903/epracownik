package com.eurosoftit.ebok.web.rest.vacation;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Subselect("SELECT oas.osp_serial as id,oas.osp_oso_serial, osp_data_od, osp_data_do, osp_liczba, osp_num_2 FROM osoba_prz oas where oas.osp_abs_serial = 5")
public class VacationExternal {
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "osp_data_od")
    private ZonedDateTime dateFrom;
    @Column(name = "osp_data_do")
    private ZonedDateTime dateTo;
    @Column(name = "osp_liczba")
    private BigDecimal days;
    @Column(name = "osp_num_2")
    private BigDecimal hours;
    @Column(name = "osp_oso_serial")
    private Long externalId;
}
