package com.eurosoftit.ebok.notification.absence;

import com.eurosoftit.ebok.domain.User;
import com.eurosoftit.ebok.web.rest.group.UserGroupEntity;
import com.eurosoftit.ebok.web.rest.vacation.Vacation;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class DailyAbsenceMailJobTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 12);

    @Test
    void oneDayLeaveHasNoDate() {
        Vacation v = fullDay("2026-10-12T00:00", "2026-10-12T00:00");
        assertThat(DailyAbsenceMailJob.line(v, TODAY, true)).isEqualTo("Kałużny Wiktor - Renault Warsztat Szczecin");
    }

    @Test
    void longerLeaveShowsDateTo() {
        Vacation v = fullDay("2026-10-12T00:00", "2026-10-16T00:00");
        assertThat(DailyAbsenceMailJob.line(v, TODAY, true)).isEqualTo("Kałużny Wiktor - Renault Warsztat Szczecin (do 16.10.2026)");
    }

    @Test
    void leaveEndingTodayHasNoDate() {
        Vacation v = fullDay("2026-10-08T00:00", "2026-10-12T00:00");
        assertThat(DailyAbsenceMailJob.line(v, TODAY, true)).isEqualTo("Kałużny Wiktor - Renault Warsztat Szczecin");
    }

    @Test
    void hourlyLeaveShowsHours() {
        Vacation v = vacation("2026-10-12T10:00", "2026-10-12T12:00", false);
        assertThat(DailyAbsenceMailJob.line(v, TODAY, true)).isEqualTo("Kałużny Wiktor - Renault Warsztat Szczecin (10:00–12:00)");
    }

    @Test
    void pendingShowsOnlyPersonAndDepartment() {
        assertThat(DailyAbsenceMailJob.line(fullDay("2026-10-12T00:00", "2026-10-16T00:00"), TODAY, false))
            .isEqualTo("Kałużny Wiktor - Renault Warsztat Szczecin");
        assertThat(DailyAbsenceMailJob.line(vacation("2026-10-12T10:00", "2026-10-12T12:00", false), TODAY, false))
            .isEqualTo("Kałużny Wiktor - Renault Warsztat Szczecin");
    }

    @Test
    void userWithoutGroupHasNoDepartment() {
        Vacation v = fullDay("2026-10-12T00:00", "2026-10-12T00:00");
        v.getCreatedBy().setGroup(null);
        assertThat(DailyAbsenceMailJob.line(v, TODAY, true)).isEqualTo("Kałużny Wiktor");
    }

    @Test
    void onlyVacationsCoveringTodayCount() {
        assertThat(DailyAbsenceMailJob.coversDay(fullDay("2026-10-12T00:00", "2026-10-12T00:00"), TODAY)).isTrue();
        assertThat(DailyAbsenceMailJob.coversDay(fullDay("2026-10-08T00:00", "2026-10-16T00:00"), TODAY)).isTrue();
        assertThat(DailyAbsenceMailJob.coversDay(vacation("2026-10-12T10:00", "2026-10-12T12:00", false), TODAY)).isTrue();
        assertThat(DailyAbsenceMailJob.coversDay(fullDay("2026-10-13T00:00", "2026-10-14T00:00"), TODAY)).isFalse();
        assertThat(DailyAbsenceMailJob.coversDay(fullDay("2026-10-05T00:00", "2026-10-09T00:00"), TODAY)).isFalse();
    }

    private static Vacation fullDay(String from, String to) {
        return vacation(from, to, true);
    }

    private static Vacation vacation(String from, String to, boolean fullDay) {
        UserGroupEntity group = new UserGroupEntity();
        group.setName("Renault Warsztat Szczecin");
        User user = new User();
        user.setFirstName("Wiktor");
        user.setLastName("Kałużny");
        user.setGroup(group);

        Vacation vacation = new Vacation();
        vacation.setCreatedBy(user);
        vacation.setFullDay(fullDay);
        vacation.setDateFrom(LocalDateTime.parse(from).atZone(ZoneId.systemDefault()));
        vacation.setDateTo(LocalDateTime.parse(to).atZone(ZoneId.systemDefault()));
        return vacation;
    }
}
