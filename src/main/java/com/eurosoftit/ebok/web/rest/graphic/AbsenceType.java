package com.eurosoftit.ebok.web.rest.graphic;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapowanie osp_abs_serial -> skrot (2-literowy w komorce) + kategoria (odcien) + oficjalny abs kod (z Absencje.odt).
 * Kategorie: A=urlop wypoczynkowy, B=rodzicielskie/opieka, C=choroba/wypadek, D=bezplatne/inne.
 * Pelna nazwa (tooltip/legenda) jest po stronie frontu w i18n (ebokApp.graphic.absence.<CODE>).
 */
public enum AbsenceType {
    UW(5, "A", "U"),
    UC(2, "B", "B"), UM(7, "B", "A"), MA(17, "B", "K"), UO(31, "B", "J"), UR(33, "B", "1"), WM(34, "B", "C"),
    DM(18, "B", "8"), ZO(24, "B", "K"), ZR(15, "B", "0"), ZS(21, "B", "0"),
    CH(4, "C", "N"), CZ(19, "C", "K"), CB(20, "C", "Q"), CN(26, "C", "K"), WY(9, "C", "N"), RH(23, "C", "K"),
    UB(1, "D", "X"), UK(22, "D", "O"), UP(40, "D", "0"), SW(3, "D", "W"), NB(6, "D", "K"), SK(14, "D", "S"),
    ZW(35, "D", "0"), OZ(32, "D", "L"), PP(28, "D", "P"), OG(27, "D", "0"), SO(16, "D", "0");

    private final int serial;
    private final String category;
    private final String absKod;

    AbsenceType(int serial, String category, String absKod) {
        this.serial = serial;
        this.category = category;
        this.absKod = absKod;
    }

    public int getSerial() {
        return serial;
    }

    public String getCategory() {
        return category;
    }

    public String getAbsKod() {
        return absKod;
    }

    public String getCode() {
        return name();
    }

    private static final Map<Long, AbsenceType> BY_SERIAL = new HashMap<>();

    static {
        for (AbsenceType t : values()) {
            BY_SERIAL.put((long) t.serial, t);
        }
    }

    public static AbsenceType fromSerial(Long serial) {
        return serial == null ? null : BY_SERIAL.get(serial);
    }
}
