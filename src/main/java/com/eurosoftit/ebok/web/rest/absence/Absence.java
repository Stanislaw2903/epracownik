package com.eurosoftit.ebok.web.rest.absence;

import com.eurosoftit.ebok.common.controller.IEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of = "id")
@IdClass(AbsenceId.class)
@Subselect("SELECT distinct a.abs_serial as id,oas.osp_serial as osp_id, oas.osp_data_od as date_from, oas.osp_data_do as date_to, abs_kod as code, oas.osp_oso_serial as external_id" +
    ",ju.first_name as first_name, ju.last_name as last_name" +
    " from absencja a join osoba_prz oas ON oas.osp_abs_serial = a.abs_serial" +
    " join jhi_user ju on ju.external_id = oas.osp_oso_serial")
public class Absence implements IEntity<Long> {
    @Id
    @Column(name = "id")
    private Long id;

    @Id
    @Column(name = "ospId")
    private Long ospId;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "date_from")
    private ZonedDateTime dateFrom;
    @Column(name = "date_to")
    private ZonedDateTime dateTo;
    @Column(name = "code")
    private String code;
    @Column(name = "external_id")
    private Long externalId;

    @Override
    public boolean isNew() {
        return id != null;
    }
}
