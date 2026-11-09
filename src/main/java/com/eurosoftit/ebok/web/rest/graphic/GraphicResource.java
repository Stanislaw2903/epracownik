package com.eurosoftit.ebok.web.rest.graphic;

import com.eurosoftit.ebok.domain.User;
import com.eurosoftit.ebok.repository.UserRepository;
import com.eurosoftit.ebok.security.AuthoritiesConstants;
import com.eurosoftit.ebok.web.rest.UserUtils;
import com.eurosoftit.ebok.web.rest.dashboard.RangesEntity;
import com.eurosoftit.ebok.web.rest.vacation.*;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import liquibase.util.BooleanUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import javax.websocket.server.PathParam;
import java.math.BigInteger;
import java.text.Collator;
import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Api
@RestController
@RequestMapping("/api/graphic")
public class GraphicResource {
    private final Logger log = LoggerFactory.getLogger(GraphicResource.class);
    private final DecimalFormat decimalFormat = new DecimalFormat("00");

    @Autowired
    private GraphicAbsenceViewRepository graphicAbsenceViewRepository;
    @Autowired
    private UserUtils userUtils;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VacationRepository vacationRepository;

    @GetMapping(params = {"!all", "!groups"})
    public Page<GraphicDTO> check(@PathParam("month") @NotNull Integer month, @PathParam("year") @NotNull Integer year,
                                  @RequestParam(value = "lastName", required = false) String lastName,
                                  Pageable pageable) {
        final List<Long> users = userUtils.getLoggedUserAndRelatedUserIds();

        log.debug("users {}", users);

        return getGraphicDTOS(month, year, pageable, users, null, lastName);
    }

    @GetMapping(params = {"all", "!groups"})
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.HR + "\")")
    public Page<GraphicDTO> getAll(@PathParam("month") @NotNull Integer month, @PathParam("year") @NotNull Integer year,
                                   @RequestParam(value = "lastName", required = false) String lastName,
                                   Pageable pageable) {
        return getGraphicDTOS(month, year, pageable, allUserIds(), null, lastName);
    }

    @GetMapping(params = {"groups", "!all"})
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.HR + "\")")
    public Page<GraphicDTO> getByGroups(@PathParam("month") @NotNull Integer month, @PathParam("year") @NotNull Integer year,
                                        @RequestParam("groups") List<Long> groups,
                                        @RequestParam(value = "lastName", required = false) String lastName,
                                        Pageable pageable) {
        if (CollectionUtils.isEmpty(groups)) {
            return Page.empty();
        }
        return getGraphicDTOS(month, year, pageable, allUserIds(), groups, lastName);
    }

    private List<Long> allUserIds() {
        return userRepository.findNativeAllIds().stream()
            .map(BigInteger::longValue)
            .collect(Collectors.toList());
    }

    private Page<GraphicDTO> getGraphicDTOS(@NotNull Integer month, @NotNull Integer year, Pageable pageable, List<Long> requestedUsers,
                                            List<Long> groups, String lastName) {
        if (CollectionUtils.isEmpty(requestedUsers)) {
            return Page.empty();
        }

        ZonedDateTime monthStart = ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneId.systemDefault());
        ZonedDateTime monthEnd = monthStart.plusMonths(1).minusDays(1);

        Map<String, Boolean> workingMap = toMap(monthStart, monthEnd);

        // batch fetch: users, absences (ERP), local accepted vacations — zamiast 3 zapytań na usera
        Map<Long, User> usersById = new HashMap<>();
        for (User u : userRepository.findAllById(requestedUsers)) {
            if (matchesFilters(u, groups, lastName)) {
                usersById.put(u.getId(), u);
            }
        }

        List<Long> users = requestedUsers.stream().distinct().filter(usersById::containsKey).collect(Collectors.toList());
        if (users.isEmpty()) {
            return Page.empty();
        }

        List<Long> externalIds = usersById.values().stream()
            .map(User::getExternalId).filter(Objects::nonNull).collect(Collectors.toList());

        Map<Long, List<GraphicAbsenceView>> absencesByExternalId = externalIds.isEmpty()
            ? new HashMap<>()
            : graphicAbsenceViewRepository.getAbsencesByRangeForUsers(monthStart, monthEnd, externalIds).stream()
                .collect(Collectors.groupingBy(GraphicAbsenceView::getExternalId));

        Map<Long, List<Vacation>> historyByUserId = vacationRepository.getHistoryForUsers(monthStart, monthEnd, users, VacationStatus.ACCEPTED).stream()
            .collect(Collectors.groupingBy(v -> v.getCreatedBy().getId()));

        List<GraphicDTO> graphics = Lists.newArrayList();
        for (Long userId : users) {
            GraphicDTO graphic = new GraphicDTO();
            User user = usersById.get(userId);

            if (user != null) {
                graphic.setFirstName(user.getFirstName());
                graphic.setLastName(user.getLastName());
            }

            for (ZonedDateTime day = monthStart; day.compareTo(monthEnd) != 1; day = day.plusDays(1)) {
                graphic.getDays().put(key(day), new GraphicDayCell(isWeekday(day) ? GraphicDayType.WORKING : GraphicDayType.WEEKEND));
            }

            Long externalId = user != null ? user.getExternalId() : null;
            if (externalId != null) {
                for (GraphicAbsenceView absence : absencesByExternalId.getOrDefault(externalId, Collections.emptyList())) {
                    markAbsence(graphic, absence.getDateFrom(), absence.getDateTo(), monthStart, monthEnd,
                        AbsenceType.fromSerial(absence.getAbsSerial()));
                }
            }

            for (Vacation vacation : historyByUserId.getOrDefault(userId, Collections.emptyList())) {
                markAbsence(graphic, vacation.getDateFrom(), vacation.getDateTo(), monthStart, monthEnd, AbsenceType.UW);
            }

            setHolidays(monthStart, monthEnd, workingMap, graphic);

            if (graphic.hasAbsence()) {
                graphics.add(graphic);
            }
        }

        if (graphics.size() == 0) {
            return Page.empty();
        }

        Collator collator = Collator.getInstance(new Locale("pl", "PL"));
        graphics.sort(Comparator
            .comparing(GraphicDTO::getLastName, Comparator.nullsLast(collator::compare))
            .thenComparing(GraphicDTO::getFirstName, Comparator.nullsLast(collator::compare)));

        final Iterable<List<GraphicDTO>> partition = Iterables.partition(graphics, pageable.getPageSize());
        return new PageImpl<>(Iterables.get(partition, pageable.getPageNumber(), new ArrayList<>()), pageable, graphics.size());
    }

    static boolean matchesFilters(User user, List<Long> groups, String lastName) {
        if (!CollectionUtils.isEmpty(groups)
            && (user.getGroup() == null || !groups.contains(user.getGroup().getId()))) {
            return false;
        }
        if (StringUtils.isNotBlank(lastName)) {
            return user.getLastName() != null
                && user.getLastName().toLowerCase(Locale.ROOT).contains(lastName.trim().toLowerCase(Locale.ROOT));
        }
        return true;
    }

    private boolean isWeekday(ZonedDateTime date) {
        return date.getDayOfWeek().getValue() < 6;
    }

    private String key(ZonedDateTime date) {
        return date.toLocalDate().toString();
    }

    private void markAbsence(GraphicDTO graphic, ZonedDateTime from, ZonedDateTime to, ZonedDateTime monthStart, ZonedDateTime monthEnd, AbsenceType type) {
        if (Objects.isNull(from) || Objects.isNull(to) || Objects.isNull(type)) {
            return;
        }
        for (ZonedDateTime start = from; start.compareTo(to) != 1; start = start.plusDays(1)) {
            if (start.compareTo(monthStart) >= 0 && start.compareTo(monthEnd) <= 0 && isWeekday(start)) {
                String k = key(start);
                GraphicDayCell current = graphic.getDays().get(k);
                // Priorytet przy nakładaniu: Choroba(C) > Urlop(A) > Rodzicielskie/opieka(B) > Inne(D).
                if (current != null && current.getType() == GraphicDayType.ABSENCE
                    && categoryPriority(current.getCategory()) >= categoryPriority(type.getCategory())) {
                    continue;
                }
                graphic.getDays().put(k, GraphicDayCell.absence(type));
            }
        }
    }

    private int categoryPriority(String category) {
        if (category == null) {
            return 0;
        }
        switch (category) {
            case "C": return 4;
            case "A": return 3;
            case "B": return 2;
            default:  return 1;
        }
    }

    private void setHolidays(ZonedDateTime monthStart, ZonedDateTime monthEnd, Map<String, Boolean> workingMap, GraphicDTO graphic) {
        for (ZonedDateTime start = monthStart; start.compareTo(monthEnd) != 1; start = start.plusDays(1)) {
            if (!isWeekday(start)) {
                continue;
            }
            Boolean isWorking = workingMap.get(start.getMonthValue() + decimalFormat.format(start.getDayOfMonth()));
            if (Objects.nonNull(isWorking) && !BooleanUtils.isTrue(isWorking)) {
                graphic.getDays().put(key(start), new GraphicDayCell(GraphicDayType.HOLIDAY));
            }
        }
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

}
