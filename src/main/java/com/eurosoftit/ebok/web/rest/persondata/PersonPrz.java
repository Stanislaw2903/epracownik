package com.eurosoftit.ebok.web.rest.persondata;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Setter
@Getter
@Entity
@EqualsAndHashCode
@Table(name = "osoba_prz")
@SequenceGenerator(name = "osoba_prz_osp_serial_seq", sequenceName = "osoba_prz_osp_serial_seq", allocationSize = 1)
public class PersonPrz {

    @Id
    @Column(name = "osp_serial")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "osoba_prz_osp_serial_seq")
    private Long id;

    @Column(name = "osp_data_od")
    private ZonedDateTime dateFrom;
    @Column(name = "osp_data_do")
    private ZonedDateTime dateTo;
    @Column(name = "osp_dod_4")
    private String kind;
    @Column(name = "osp_dod_3")
    private String sanitaryTests;
    @Column(name = "osp_vchar_1")
    private String comments;
    @Column(name = "osp_typ")
    private String type;

    @Column(name = "osp_oso_serial")
    private Long externalId;

    @Column(name = "osp_liczba")
    private BigDecimal vacationDays; //liczba dni urlopu

    @Column(name = "osp_kwota")
    private BigDecimal toPay;

    @Column(name = "osp_abs_serial")
    private Long absSerial;

    @Column(name = "osp_tyt_serial")
    private Long tytSerial;
}

