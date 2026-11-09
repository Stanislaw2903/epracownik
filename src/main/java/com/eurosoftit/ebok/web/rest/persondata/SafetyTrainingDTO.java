package com.eurosoftit.ebok.web.rest.persondata;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
public class SafetyTrainingDTO implements Serializable {
    private ZonedDateTime dateFrom;
    private ZonedDateTime dateTo;
    private String kind;
    private String comments;
    private Boolean expires = Boolean.FALSE;

    public SafetyTrainingDTO(ZonedDateTime dateFrom, ZonedDateTime dateTo, String kind, String comments) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.kind = kind;
        this.comments = comments;

        if(Objects.nonNull(dateTo))
            this.expires = !dateTo.isAfter(ZonedDateTime.now().minusDays(60));
    }
}
