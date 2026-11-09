package com.eurosoftit.ebok.web.rest.vacation;

import com.eurosoftit.ebok.common.ApplicationContextProvider;
import com.eurosoftit.ebok.service.AbstractSearchService;
import com.eurosoftit.ebok.web.rest.UserUtils;
import com.eurosoftit.ebok.web.rest.dashboard.AbsencesView;
import com.eurosoftit.ebok.web.rest.dashboard.RangesEntity;
import com.eurosoftit.ebok.web.rest.dashboard.VacationBalance;
import com.eurosoftit.ebok.web.rest.dashboard.VestedVacationView;
import com.google.common.collect.Maps;
import liquibase.util.BooleanUtils;
import lombok.Getter;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import javax.websocket.server.PathParam;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class VacationService extends AbstractSearchService<Vacation, Long, VacationAdminProperties> {

    private final DecimalFormat decimalFormat = new DecimalFormat("00");

    @Autowired
    @Getter
    private VacationRepository repository;
    @Autowired
    private VacationExternalRepository vacationExternalRepository;
    @Autowired
    private UserUtils userUtils;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public JpaSpecificationExecutor getSearchRepository() {
        return repository;
    }

    public Collection<Long> getHistoryByYearForUser(Integer yearNumber) {

        if (Objects.isNull(yearNumber)) {
            return Collections.emptyList();
        }

        ZonedDateTime start = ZonedDateTime.of(yearNumber, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault());
        ZonedDateTime end = start.plusYears(1).minusSeconds(1);
        List<VacationExternal> vacations = vacationExternalRepository.getHistoryByYearForUser(start, end, userUtils.getExternalUserId());
//TODO
//        la danego pracownika po osp_oso_serial i dla kolumny „osp_abs_serial” = 5
//        Okres: „osp_data_od – „osp_data_do”   Dni – kolumna „osp_liczba”  Godzin – kolumna „osp_num_2”


        Map<Integer, Long> months = Maps.newHashMap();
        init(months);

        for (VacationExternal vacation : vacations) {
            ZonedDateTime dateFrom = vacation.getDateFrom();
            ZonedDateTime dateTo = vacation.getDateTo();

            if(Objects.isNull(dateFrom) || Objects.isNull(dateTo)) {
                continue;
            }

            Map<String, Boolean> map = toMap(dateFrom, dateTo);

            if (dateFrom.getMonthValue() == dateTo.getMonthValue() && dateFrom.getYear() == dateTo.getYear()) {
//                if(BooleanUtils.isTrue(vacation.getFullDay())){
                List<ZonedDateTime> dateList = getDays(dateFrom, dateTo, map);

                Long monthValue = months.getOrDefault(dateFrom.getMonthValue(), 0l);
                months.put(dateFrom.getMonthValue(), monthValue + dateList.size() * 8);

//                } else {
//                    addMonthValue(months, dateFrom.getMonthValue(), ChronoUnit.HOURS.between(dateFrom, dateTo));
//                }
            } else if (dateFrom.getMonthValue() != dateTo.getMonthValue() || dateFrom.getYear() != dateTo.getYear()) {
                ZonedDateTime endOfMonthFrom = dateFrom.truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1).plusMonths(1).minusDays(1);;
                ZonedDateTime startOfMonthTo = dateTo.truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1);

//                if (BooleanUtils.isTrue(vacation.getFullDay())) {
                if (dateFrom.getYear() == yearNumber) {
                    List<ZonedDateTime> dateList = getDays(dateFrom, endOfMonthFrom, map);
                    Long monthValue = months.getOrDefault(dateFrom.getMonthValue(), 0l);
                    months.put(dateFrom.getMonthValue(), monthValue + dateList.size() * 8);
                }

                if (dateTo.getYear() == yearNumber) {
                    List<ZonedDateTime> dateList = getDays(startOfMonthTo, dateTo.plusDays(1), map);
                    Long monthValue = months.getOrDefault(startOfMonthTo.getMonthValue(), 0l);
                    months.put(dateTo.getMonthValue(), monthValue + dateList.size() * 8);
                }
//                } else {
//                    addMonthValue(months, dateFrom.getMonthValue(), ChronoUnit.HOURS.between(dateFrom, endOfMonthFrom));
//                    addMonthValue(months, dateTo.getMonthValue(), ChronoUnit.HOURS.between(startOfMonthTo, dateTo));
//                }
            }
        }

        return months.values();
    }

    private void init(Map<Integer, Long> months) {
        for (int i = 1; i <= 12; i++) {
            months.put(i,0l);
        }
    }

    public Collection<VacationDetailsHistoryDTO> getHistoryDetailsByYearForUser(Integer yearNumber) {
        return getHistoryDetailsByYearForExternalId(yearNumber, userUtils.getExternalUserId());
    }

    public Collection<VacationDetailsHistoryDTO> getHistoryDetailsByYearForExternalId(Integer yearNumber, Long externalId) {
        if (Objects.isNull(yearNumber)) {
            return Collections.emptyList();
        }

        ZonedDateTime start = ZonedDateTime.of(yearNumber, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault());
        ZonedDateTime end = start.plusYears(1).minusSeconds(1);
        List<VacationExternal> vacations = vacationExternalRepository.getHistoryByYearForUser(start, end, externalId);
//TODO
//        la danego pracownika po osp_oso_serial i dla kolumny „osp_abs_serial” = 5
//        Okres: „osp_data_od – „osp_data_do”   Dni – kolumna „osp_liczba”  Godzin – kolumna „osp_num_2”

        List<VacationDetailsHistoryDTO> months = Lists.newArrayList();
        for (VacationExternal vacation : vacations) {
            ZonedDateTime dateFrom = vacation.getDateFrom();
            ZonedDateTime dateTo = vacation.getDateTo();

            if(Objects.isNull(dateFrom) || Objects.isNull(dateTo)) {
                continue;
            }

            Map<String, Boolean> map = toMap(dateFrom, dateTo);

            List<ZonedDateTime> dateList = getDays(dateFrom, dateTo, map);

            VacationDetailsHistoryDTO historyDTO = new VacationDetailsHistoryDTO();
            historyDTO.setDateTo(dateTo);
            historyDTO.setDateFrom(dateFrom);
            historyDTO.setDaysTaken(vacation.getDays() != null ? vacation.getDays() : BigDecimal.valueOf(dateList.size()));
            historyDTO.getDays().addAll(dateList);
            months.add(historyDTO);
        }

        months.sort(Comparator.comparing(VacationDetailsHistoryDTO::getDateFrom));
        return months;
    }

    private List<ZonedDateTime> getDays(ZonedDateTime dateFrom, ZonedDateTime dateTo, Map<String, Boolean> workingMap) {

        List<ZonedDateTime> workingDays = Lists.newArrayList();
        for (ZonedDateTime start = dateFrom; start.compareTo(dateTo) != 1 ; start = start.plusDays(1)) {
            if(BooleanUtils.isTrue(workingMap.get(start.getMonthValue() + decimalFormat.format(start.getDayOfMonth() )))){
                workingDays.add(start);
            }
        }

        return workingDays;
    }


    private Map<String, Boolean> toMap(@PathParam("dateFrom") @NotNull ZonedDateTime dateFrom, @PathParam("dateTo") @NotNull ZonedDateTime dateTo) {
        List<Long> max = (List<Long>) entityManager.createQuery("SELECT max(r.id) FROM RangesEntity r WHERE r.dateFrom <= :dateTo and r.dateTo >= :dateFrom group by r.dateFrom, r.dateTo")
            .setParameter("dateFrom", dateFrom).setParameter("dateTo", dateTo).getResultList().stream().filter(Objects::nonNull).collect(Collectors.toList());

        List<RangesEntity> result = entityManager.createQuery("SELECT r FROM RangesEntity r WHERE r.dateFrom <= :dateTo and r.dateTo >= :dateFrom and r.id in(:maxIds)", RangesEntity.class)
            .setParameter("dateFrom", dateFrom).setParameter("dateTo", dateTo).setParameter("maxIds", max).getResultList().stream().filter(Objects::nonNull).collect(Collectors.toList());

        Map<String, Boolean> map = Maps.newHashMap();
        for (RangesEntity rangesEntity : result) {
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(1), rangesEntity.getDay1());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(2), rangesEntity.getDay2());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(3), rangesEntity.getDay3());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(4), rangesEntity.getDay4());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(5), rangesEntity.getDay5());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(6), rangesEntity.getDay6());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(7), rangesEntity.getDay7());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(8), rangesEntity.getDay8());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(9), rangesEntity.getDay9());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(10), rangesEntity.getDay10());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(11), rangesEntity.getDay11());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(12), rangesEntity.getDay12());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(13), rangesEntity.getDay13());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(14), rangesEntity.getDay14());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(15), rangesEntity.getDay15());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(16), rangesEntity.getDay16());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(17), rangesEntity.getDay17());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(18), rangesEntity.getDay18());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(19), rangesEntity.getDay19());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(20), rangesEntity.getDay20());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(21), rangesEntity.getDay21());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(22), rangesEntity.getDay22());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(23), rangesEntity.getDay23());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(24), rangesEntity.getDay24());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(25), rangesEntity.getDay25());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(26), rangesEntity.getDay26());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(27), rangesEntity.getDay27());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(28), rangesEntity.getDay28());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(29), rangesEntity.getDay29());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(30), rangesEntity.getDay30());
            map.put(rangesEntity.getDateFrom().getMonthValue()+decimalFormat.format(31), rangesEntity.getDay31());
        }

        return map;
    }


    @Override
    public Vacation save(Vacation entity) {
        return super.save(entity);
    }

    public void isValid(Vacation entity) {
        UserUtils userUtils = ApplicationContextProvider.getBean(UserUtils.class);
        VacationRepository vacationRepository = ApplicationContextProvider.getBean(VacationRepository.class);
        ZonedDateTime start = ZonedDateTime.now(ZoneId.systemDefault()).withDayOfMonth(1).withSecond(0).withMinute(0).withDayOfYear(1).withHour(0).withMonth(1).withNano(0);
        ZonedDateTime end = start.plusYears(1).minusSeconds(1);

        if (VacationType.ANNUAL_LEAVE.equals(entity.getVacationType()) && Boolean.FALSE.equals(entity.getFullDay())) {
            throw new ValidationException("HOURLY_NOT_ALLOWED");
        }

        if (entity.getDateFrom() != null) {
            ZonedDateTime today = ZonedDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS);
            if (entity.getDateFrom().isBefore(today)) {
                throw new ValidationException("PAST_DATE");
            }
        }

        if(VacationType.LEAVE_ON_REQUEST.equals(entity.getVacationType()) && vacationRepository.vacationOnRequestCount(userUtils.getLoggedUserId(), start, end) >= 4) {
            throw new ValidationException("TOO_MANY_ON_REQUEST");
        }

        if (entity.getDateFrom() != null && entity.getDateTo() != null) {
            Long overlapping = vacationRepository.countOverlapping(userUtils.getLoggedUserId(), entity.getDateFrom(), entity.getDateTo());
            if (overlapping != null && overlapping > 0) {
                throw new ValidationException("OVERLAP");
            }
        }
    }

    private boolean isVacationTypeWithLimit(VacationType type) {
        return VacationType.ANNUAL_LEAVE.equals(type) || VacationType.LEAVE_ON_REQUEST.equals(type);
    }

    public Double getVestedDays(Long externalId) {
        Long year = (long) ZonedDateTime.now().getYear();
        List<VestedVacationView> vestedList = entityManager.createQuery("select v from VestedVacationView v where v.externalId = :externalId and v.year = :year", VestedVacationView.class)
            .setParameter("externalId", externalId).setParameter("year", year).getResultList();
        if (vestedList.isEmpty()) {
            return null;
        }
        List<AbsencesView> absencesList = entityManager.createQuery("select v from AbsencesView v where v.externalId = :externalId and v.year = :year", AbsencesView.class)
            .setParameter("externalId", externalId).setParameter("year", year).getResultList();
        AbsencesView absences = absencesList.isEmpty() ? null : absencesList.get(0);
        return VacationBalance.remainingDays(vestedList.get(0), absences).doubleValue();
    }

    public Double availableVacationDaysForSubmission(Long externalId, Long userId) {
        Double vested = getVestedDays(externalId);
        if (vested == null) {
            return null;
        }
        BigDecimal pending = repository.sumPendingUsed(userId,
            Arrays.asList(VacationStatus.WAITING, VacationStatus.ACCEPTED_INITIALLY),
            Arrays.asList(VacationType.ANNUAL_LEAVE, VacationType.LEAVE_ON_REQUEST));
        double pendingDays = pending == null ? 0.0 : pending.doubleValue();
        return vested - pendingDays;
    }

    public boolean hasEnoughForAcceptance(Long externalId, Vacation vacation) {
        if (!isVacationTypeWithLimit(vacation.getVacationType())) {
            return true;
        }
        Double vested = getVestedDays(externalId);
        if (vested == null) {
            return true;
        }
        BigDecimal requested = vacation.getUsed() == null ? BigDecimal.ZERO : vacation.getUsed();
        return requested.doubleValue() <= vested;
    }

    public Vacation findLatest(@Param("userId") Long userId) {
        return repository.findLatest(userId);
    }

    public List<Long> findIdsWithStatusVacationWaitingAndTwoLevelAndInitialApproval(List<Long> userIds) {

        if(CollectionUtils.isEmpty(userIds)) {
            return userIds;
        }

        return repository.findIdsWithStatusVacationWaitingAndTwoLevelAndInitialApproval(userIds);
    }
}
