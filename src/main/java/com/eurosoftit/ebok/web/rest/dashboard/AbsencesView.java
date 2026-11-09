package com.eurosoftit.ebok.web.rest.dashboard;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
@Subselect("SELECT osp_oso_serial as external_id, sum(osp_num_2) as used, EXTRACT(YEAR FROM osp_data_od) as _year from osoba_prz where osp_abs_serial = 5 group by osp_oso_serial, EXTRACT(YEAR FROM osp_data_od)")
@Immutable
public class AbsencesView {
    @Id
    @Column(name = "external_id")
    private Long externalId;
    @Column(name = "_year")
    private Long year;
    @Column(name = "used")
    private Long usedHours;
}
