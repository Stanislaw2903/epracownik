package com.eurosoftit.ebok.web.rest.dashboard;

import java.math.BigDecimal;

public final class VacationBalance {

    private VacationBalance() {
    }

    public static BigDecimal usedDays(AbsencesView absences) {
        return absences == null ? BigDecimal.ZERO : zeroIfNull(absences.getUsedDays());
    }

    public static BigDecimal vestedDays(VestedVacationView vested) {
        if (vested == null) {
            return BigDecimal.ZERO;
        }
        return zeroIfNull(vested.getVested()).add(zeroIfNull(vested.getShiftVested()));
    }

    public static BigDecimal remainingDays(VestedVacationView vested, AbsencesView absences) {
        return vestedDays(vested).subtract(usedDays(absences));
    }

    private static BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
