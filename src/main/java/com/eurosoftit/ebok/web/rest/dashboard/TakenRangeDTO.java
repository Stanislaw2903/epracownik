package com.eurosoftit.ebok.web.rest.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TakenRangeDTO {
    private Double used;
    private Double tooMuch;
    private Double workingDays;
}
