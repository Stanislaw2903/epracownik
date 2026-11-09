package com.eurosoftit.ebok.web.rest.vacation;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class VacationHistoryProperties {
    private ZonedDateTime year;
}
