package com.eurosoftit.ebok.web.rest.persondata;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@EqualsAndHashCode
@Table(name = "dane_hi_da")
public class DataHiDa {

    @Id
    @Column(name = "dhi_serial")
    private Long id;
    @Column(name = "dhi_vchar_1")
    private String name;
    @Column(name = "dhi_date_1")
    private ZonedDateTime completionDate;
    @Column(name = "dhi_date_2")
    private ZonedDateTime dateTo;
    @Column(name = "dhi_char_2")
    private String donated;
    @Column(name = "dhi_kon_serial")
    private Long serial;

    @Column(name = "dhi_oso_serial")
    private Long externalId;
}
