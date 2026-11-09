package com.eurosoftit.ebok.web.rest.absence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;
import java.time.ZonedDateTime;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class AbsenceDTO implements Serializable {
    private Long id;
    private Long ospId;
    private ZonedDateTime dateFrom;
    private ZonedDateTime dateTo;
    private String code;
    private String firstName;
    private String lastName;
}
