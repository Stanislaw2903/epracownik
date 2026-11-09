package com.eurosoftit.ebok.web.rest.vacation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class VacationDTO {
    @JsonIgnore
    private Long externalId;
    private Long numberOfWorkingDays;
    private Long numberOfObligatoryDays;
}
