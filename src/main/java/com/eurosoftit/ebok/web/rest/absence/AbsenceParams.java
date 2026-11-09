package com.eurosoftit.ebok.web.rest.absence;

import com.eurosoftit.ebok.domain.specification.DateFrom;
import com.eurosoftit.ebok.domain.specification.DateTo;
import com.eurosoftit.ebok.domain.specification.Equal;
import com.eurosoftit.ebok.domain.specification.Like;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class AbsenceParams {
    @DateFrom
    private ZonedDateTime dateFrom;
    @DateTo
    private ZonedDateTime dateTo;
    @Equal
    private String code;
    @Like
    private String lastName;
    @Like
    private String firstName;
    @Equal(field = "externalId")
    private List<Long> externalIds;
}
