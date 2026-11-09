package com.eurosoftit.ebok.web.rest.dashboard;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class VacationBalanceTest {

    @Test
    void sevenHourWorkerTakingOneDayUsesOneDay() {
        VestedVacationView vested = vested("10", null);
        AbsencesView absences = absences("1", "7");

        assertThat(VacationBalance.usedDays(absences)).isEqualByComparingTo("1");
        assertThat(VacationBalance.remainingDays(vested, absences)).isEqualByComparingTo("9");
    }

    @Test
    void entitlementIsYearlyPlusCarriedOver() {
        VestedVacationView vested = vested("26", "9");
        AbsencesView absences = absences("19", "132.8");

        assertThat(VacationBalance.vestedDays(vested)).isEqualByComparingTo("35");
        assertThat(VacationBalance.remainingDays(vested, absences)).isEqualByComparingTo("16");
    }

    @Test
    void fractionalDaysAreKept() {
        assertThat(VacationBalance.remainingDays(vested("26", "0.5"), absences("1.5", null))).isEqualByComparingTo("25");
    }

    @Test
    void missingDataCountsAsZero() {
        assertThat(VacationBalance.usedDays(null)).isEqualByComparingTo("0");
        assertThat(VacationBalance.remainingDays(vested("20", null), null)).isEqualByComparingTo("20");
        assertThat(VacationBalance.vestedDays(null)).isEqualByComparingTo("0");
    }

    private static VestedVacationView vested(String yearly, String carriedOver) {
        VestedVacationView view = new VestedVacationView();
        view.setVested(yearly == null ? null : new BigDecimal(yearly));
        view.setShiftVested(carriedOver == null ? null : new BigDecimal(carriedOver));
        return view;
    }

    private static AbsencesView absences(String days, String hours) {
        AbsencesView view = new AbsencesView();
        view.setUsedDays(days == null ? null : new BigDecimal(days));
        view.setUsedHours(hours == null ? null : new BigDecimal(hours));
        return view;
    }
}
