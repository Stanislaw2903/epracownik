package com.eurosoftit.ebok.web.rest.graphic;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class GraphicDTO {
    private String firstName;
    private String lastName;
    private Map<String, GraphicDayCell> days = new LinkedHashMap<>();

    public boolean isAllWorking() {
        return days.values().stream().allMatch(c -> c.getType() == GraphicDayType.WORKING);
    }

    public boolean hasAbsence() {
        return days.values().stream().anyMatch(c -> c.getType() == GraphicDayType.ABSENCE);
    }
}
