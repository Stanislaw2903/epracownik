package com.eurosoftit.ebok.web.rest.graphic;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Immutable
@Subselect("SELECT oas.osp_serial as id, oas.osp_oso_serial, oas.osp_data_od, oas.osp_data_do, oas.osp_abs_serial FROM osoba_prz oas WHERE oas.osp_abs_serial in (1,2,3,4,5,6,7,9,14,15,16,17,18,19,20,21,22,23,24,26,27,28,31,32,33,34,35,40)")
public class GraphicAbsenceView {
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "osp_data_od")
    private ZonedDateTime dateFrom;
    @Column(name = "osp_data_do")
    private ZonedDateTime dateTo;
    @Column(name = "osp_oso_serial")
    private Long externalId;
    @Column(name = "osp_abs_serial")
    private Long absSerial;
}
