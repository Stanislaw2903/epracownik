package com.eurosoftit.ebok.web.rest.vacation;

import com.eurosoftit.ebok.domain.specification.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@DateToAndFromInRange
public class VacationAdminProperties {
    @Like
    private String comment;
    @DateEquals
    private ZonedDateTime createdDate;
    private ZonedDateTime dateFrom;
    private ZonedDateTime dateTo;
    @DateFrom(filed = "dateFrom")
    private ZonedDateTime timeFrom;
    @DateTo(filed = "dateTo")
    private ZonedDateTime timeTo;
    @Equal
    private Boolean fullDay;
    @Like(field = "createdBy.login")
    private String createdBy;
    @Equal
    private List<VacationStatus> status;
    @Like
    private String statusDescription;
    @Like(field = "processedBy.login")
    private String processedBy;
    @Equal(field = "createdBy.id")
    private Long userId;

    @Equal(field = "createdBy.id")
    private List<Long> relatedUsers;

    @Equal
    private VacationType vacationType;

    @JsonIgnore
    @Equal(field = "id")
    private List<Long> vacationIds;
}
