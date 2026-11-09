package com.eurosoftit.ebok.web.rest.integration;

import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Subselect("select o.oso_serial,o.oso_imie1,o.oso_nazwisko,o.oso_email,o.oso_typ, o.oso_data_zwol from osoba o where not exists(select 1 from jhi_user jh where jh.external_id = o.oso_serial)")
public class PersonNotInSystem {
    @Id
    @Column(name = "oso_serial")
    private Long id;

    @Column(name = "oso_data_zwol")
    private ZonedDateTime osoDataZwol;
    @Column(name = "oso_imie1")
    private String firstName;
    @Column(name = "oso_nazwisko")
    private String lastName;
    @Column(name = "oso_email")
    private String email;
    @Column(name = "oso_typ")
    private String type;

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }
}
