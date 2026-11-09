package com.eurosoftit.ebok.web.rest.graphic;

import lombok.Data;

@Data
public class GraphicDayCell {
    private GraphicDayType type;
    private String code;        // tylko dla ABSENCE (np. "UW")
    private String category;    // tylko dla ABSENCE (A/B/C/D -> odcien)
    private String absKod;      // tylko dla ABSENCE - oficjalny kod z Absencje.odt (U/K/J...)

    public GraphicDayCell() {
    }

    public GraphicDayCell(GraphicDayType type) {
        this.type = type;
    }

    public static GraphicDayCell absence(AbsenceType t) {
        GraphicDayCell c = new GraphicDayCell(GraphicDayType.ABSENCE);
        c.setCode(t.getCode());
        c.setCategory(t.getCategory());
        c.setAbsKod(t.getAbsKod());
        return c;
    }
}
