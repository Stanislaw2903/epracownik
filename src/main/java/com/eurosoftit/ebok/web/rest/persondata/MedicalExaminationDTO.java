package com.eurosoftit.ebok.web.rest.persondata;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class MedicalExaminationDTO implements Serializable {
    private ZonedDateTime dateFrom;
    private ZonedDateTime dateTo;
    private String kind;
    private String sanitaryTests;
    private String comments;
    private Boolean expires = Boolean.FALSE;

    public MedicalExaminationDTO(ZonedDateTime dateFrom, ZonedDateTime dateTo, String kind, String sanitaryTests, String comments) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.kind = kind;
        this.sanitaryTests = sanitaryTests;
        this.comments = comments;

        if(Objects.nonNull(dateTo))
            this.expires = !dateTo.isAfter(ZonedDateTime.now().minusDays(60));
    }
}
