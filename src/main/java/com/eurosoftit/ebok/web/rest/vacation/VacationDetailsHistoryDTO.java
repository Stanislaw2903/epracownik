package com.eurosoftit.ebok.web.rest.vacation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.compress.utils.Lists;

import java.time.ZonedDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class VacationDetailsHistoryDTO {
    private ZonedDateTime dateFrom;
    private ZonedDateTime dateTo;
    private long daysTaken;
    private List<ZonedDateTime> days = Lists.newArrayList();
}
