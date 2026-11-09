package com.eurosoftit.ebok.web.rest.vacation;

import com.eurosoftit.ebok.domain.AbstractEntity;
import com.eurosoftit.ebok.domain.User;
import com.eurosoftit.ebok.domain.validation.MaxHours;
import com.eurosoftit.ebok.domain.validation.MaxHoursFieldCondition;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Setter
@Getter
@EqualsAndHashCode
@Table(name = "vacation")
@MaxHours(filedFrom = "dateFrom",filedTo = "dateTo", max = 8, checkIf = @MaxHoursFieldCondition(name = "fullDay", value = "false"))
public class Vacation extends AbstractEntity {
    @Column(name = "comment")
    private String comment;
    @Column(name = "date_from")
    private ZonedDateTime dateFrom;
    @Column(name = "date_to")
    private ZonedDateTime dateTo;
    @Column(name = "full_day")
    private Boolean fullDay;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private VacationStatus status;
    @Column(name = "status_description")
    private String statusDescription;
    @ManyToOne
    private User processedBy;

    @ManyToOne
    private User createdBy;
    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    @Column(name = "used")
    private BigDecimal used;

    @Column(name = "erp_id")
    private Long erpId;

    @Enumerated(EnumType.STRING)
    private VacationType vacationType;
}
