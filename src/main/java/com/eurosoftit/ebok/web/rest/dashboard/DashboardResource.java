package com.eurosoftit.ebok.web.rest.dashboard;

import com.eurosoftit.ebok.security.AuthoritiesConstants;
import com.eurosoftit.ebok.web.rest.UserUtils;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import liquibase.util.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.websocket.server.PathParam;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Api
@RestController
@RequestMapping("/api/dashboard")
public class DashboardResource {
    private final Logger log = LoggerFactory.getLogger(DashboardResource.class);
    private final DecimalFormat decimalFormat = new DecimalFormat("00");

    @Autowired
    private UserUtils userUtils;
    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/calculate")
    public TakenRangeDTO check(@PathParam("dateFrom") @NotNull ZonedDateTime dateFrom, @PathParam("dateTo") @NotNull ZonedDateTime dateTo) {
        dateFrom = dateFrom.truncatedTo(ChronoUnit.DAYS);
        dateTo = dateTo.truncatedTo(ChronoUnit.DAYS);

        VacationDTO vacation = vacation();
        Map<String, Boolean> map = toMap(dateFrom, dateTo);

        double userWantToTake = 0.0;

        for (ZonedDateTime start = dateFrom; start.compareTo(dateTo) != 1; start = start.plusDays(1)) {
            if (BooleanUtils.isTrue(map.get(start.getMonthValue() + decimalFormat.format(start.getDayOfMonth())))) {
                userWantToTake++;
            }
        }

        double realValue = Optional.ofNullable(vacation.getVested()).orElse(0.0) - userWantToTake;

        if (realValue < 0.0) {
            return new TakenRangeDTO(Optional.ofNullable(vacation.getVested()).orElse(0.0), Math.abs(realValue), userWantToTake);
        }

        return new TakenRangeDTO(userWantToTake, null, userWantToTake);
    }

    private Map<String, Boolean> toMap(@PathParam("dateFrom") @NotNull ZonedDateTime dateFrom, @PathParam("dateTo") @NotNull ZonedDateTime dateTo) {
        List<Long> max = (List<Long>) entityManager.createQuery("SELECT max(r.id) FROM RangesEntity r WHERE r.dateFrom <= :dateTo and r.dateTo >= :dateFrom group by r.dateFrom, r.dateTo")
            .setParameter("dateFrom", dateFrom).setParameter("dateTo", dateTo).getResultList().stream().filter(Objects::nonNull).collect(Collectors.toList());

        List<RangesEntity> result = entityManager.createQuery("SELECT r FROM RangesEntity r WHERE r.dateFrom <= :dateTo and r.dateTo >= :dateFrom and r.id in(:maxIds)", RangesEntity.class)
            .setParameter("dateFrom", dateFrom).setParameter("dateTo", dateTo).setParameter("maxIds", max).getResultList().stream().filter(Objects::nonNull).collect(Collectors.toList());

        Map<String, Boolean> map = Maps.newHashMap();
        for (RangesEntity rangesEntity : result) {
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(1), rangesEntity.getDay1());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(2), rangesEntity.getDay2());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(3), rangesEntity.getDay3());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(4), rangesEntity.getDay4());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(5), rangesEntity.getDay5());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(6), rangesEntity.getDay6());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(7), rangesEntity.getDay7());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(8), rangesEntity.getDay8());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(9), rangesEntity.getDay9());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(10), rangesEntity.getDay10());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(11), rangesEntity.getDay11());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(12), rangesEntity.getDay12());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(13), rangesEntity.getDay13());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(14), rangesEntity.getDay14());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(15), rangesEntity.getDay15());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(16), rangesEntity.getDay16());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(17), rangesEntity.getDay17());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(18), rangesEntity.getDay18());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(19), rangesEntity.getDay19());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(20), rangesEntity.getDay20());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(21), rangesEntity.getDay21());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(22), rangesEntity.getDay22());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(23), rangesEntity.getDay23());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(24), rangesEntity.getDay24());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(25), rangesEntity.getDay25());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(26), rangesEntity.getDay26());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(27), rangesEntity.getDay27());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(28), rangesEntity.getDay28());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(29), rangesEntity.getDay29());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(30), rangesEntity.getDay30());
            map.put(rangesEntity.getDateFrom().getMonthValue() + decimalFormat.format(31), rangesEntity.getDay31());
        }

        return map;
    }

    @GetMapping("/vacation")
    @Transactional
    VacationDTO vacation() {
        log.debug("REST request to get vacation");
        return vacationForExternalId(userUtils.getExternalUserId());
    }

    @GetMapping("/vacation/{externalId}")
    @Transactional
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")" + " OR " + "hasAuthority(\"" + AuthoritiesConstants.SYSTEM + "\")" + " OR " + "hasAuthority(\"" + AuthoritiesConstants.HR + "\")")
    VacationDTO vacationForUser(@PathVariable("externalId") Long externalId) {
        log.debug("REST request to get vacation for external id {}", externalId);
        return vacationForExternalId(externalId);
    }

    private VacationDTO vacationForExternalId(Long externalId) {
        Double used = 0.0;
        Double vested = 0.0;

        List<VestedVacationView> vestedVacationView = entityManager.createQuery("select v from VestedVacationView v where v.externalId =:externalId and v.year =:year", VestedVacationView.class)
            .setParameter("externalId", externalId).setParameter("year", Long.valueOf(ZonedDateTime.now().getYear())).getResultList();

        List<AbsencesView> absencesView = entityManager.createQuery("select v from AbsencesView v where v.externalId =:externalId and v.year =:year", AbsencesView.class)
            .setParameter("externalId", externalId).setParameter("year", Long.valueOf(ZonedDateTime.now().getYear())).getResultList();

        if (absencesView != null && !absencesView.isEmpty()) {
            used = (absencesView.get(0).getUsedHours() == null ? 0 : absencesView.get(0).getUsedHours()) / 8.0;
        }

        if (vestedVacationView != null && !vestedVacationView.isEmpty()) {

            VestedVacationView vacationView = vestedVacationView.get(0);

            Long vacationVested = vacationView.getVested();
            Long shiftVested = vacationView.getShiftVested();

            if (absencesView != null && !absencesView.isEmpty()) {
                Long usedHours = absencesView.get(0).getUsedHours();
                vested = (zeroIfNull(vacationVested) * 8 + zeroIfNull(shiftVested) * 8 - zeroIfNull(usedHours)) / 8.0;
            } else {
                vested = Double.valueOf(zeroIfNull(vacationVested) + zeroIfNull(shiftVested));
            }
        }

        return new VacationDTO(used, vested);
    }

    Long zeroIfNull(Long value) {
        return (value == null ? 0 : value);
    }
}
