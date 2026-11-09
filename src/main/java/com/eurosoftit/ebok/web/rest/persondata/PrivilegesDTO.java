package com.eurosoftit.ebok.web.rest.persondata;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class PrivilegesDTO implements Serializable {
    private String name;
    private ZonedDateTime completionDate;
    private ZonedDateTime dateTo;
    private Boolean expires = Boolean.FALSE;

    public PrivilegesDTO(String name, ZonedDateTime completionDate, ZonedDateTime dateTo) {
        this.name = name;
        this.completionDate = completionDate;
        this.dateTo = dateTo;

        if(Objects.nonNull(dateTo))
            this.expires = !dateTo.isAfter(ZonedDateTime.now().minusDays(60));
    }
}
