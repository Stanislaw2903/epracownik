package com.eurosoftit.ebok.web.rest.vacation;

import com.eurosoftit.ebok.domain.specification.DateFrom;
import com.eurosoftit.ebok.domain.specification.DateTo;
import com.eurosoftit.ebok.domain.specification.Equal;
import com.eurosoftit.ebok.domain.specification.Like;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
public class VacationProperties {
    @Like
    private String comment;
    @DateFrom
    private ZonedDateTime dateFrom;
    @DateTo
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
    @Equal
    private VacationType vacationType;

    public VacationAdminProperties toAdmin() {
        VacationAdminProperties vacationAdminProperties = new VacationAdminProperties();
        vacationAdminProperties.setComment(this.getComment());
        vacationAdminProperties.setCreatedBy(this.getCreatedBy());
        vacationAdminProperties.setDateFrom(this.getDateFrom());
        vacationAdminProperties.setDateTo(this.getDateTo());
        vacationAdminProperties.setFullDay(this.getFullDay());
        vacationAdminProperties.setProcessedBy(this.getProcessedBy());
        vacationAdminProperties.setStatus(this.getStatus());
        vacationAdminProperties.setStatusDescription(this.getStatusDescription());
        vacationAdminProperties.setTimeFrom(this.getTimeFrom());
        vacationAdminProperties.setTimeTo(this.getTimeTo());

        return vacationAdminProperties;
    }
}
