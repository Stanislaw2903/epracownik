package com.eurosoftit.ebok.web.rest.vacation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public interface VacationRepository extends JpaRepository<Vacation, Long>, JpaSpecificationExecutor<Vacation> {
    @Query("select v from Vacation v where v.createdBy.id = :userId and (v.dateFrom <= :startYear or v.dateTo <= :endYear)")
    List<Vacation> getHistoryByYearForUser(@Param("startYear") ZonedDateTime startYear, @Param("endYear") ZonedDateTime endYear, @Param("userId") Long loggedUserId);

    @Query("select count(v.id) from Vacation v where v.createdBy.id = :userId and (v.dateFrom <= :startYear or v.dateTo <= :endYear) and v.vacationType = 'LEAVE_ON_REQUEST'")
    Long vacationOnRequestCount(@Param("userId") Long userId, @Param("startYear") ZonedDateTime startYear, @Param("endYear") ZonedDateTime endYear);

    @Query("select count(v.id) from Vacation v where v.createdBy.id = :userId and v.status <> 'REJECTED' and v.status <> 'CANCELLED' and v.dateFrom <= :dateTo and v.dateTo >= :dateFrom")
    Long countOverlapping(@Param("userId") Long userId, @Param("dateFrom") ZonedDateTime dateFrom, @Param("dateTo") ZonedDateTime dateTo);

    @Query("select coalesce(sum(v.used), 0) from Vacation v where v.createdBy.id = :userId and v.status in :statuses and v.vacationType in :types")
    BigDecimal sumPendingUsed(@Param("userId") Long userId, @Param("statuses") List<VacationStatus> statuses, @Param("types") List<VacationType> types);

    @Query("select v from Vacation v where v.createdBy.id = :userId and (v.dateFrom <= :dateTo or v.dateTo >= :dateFrom) and v.status = :status")
    List<Vacation> getHistory(@Param("dateFrom") ZonedDateTime dateFrom, @Param("dateTo") ZonedDateTime dateTo, @Param("userId") Long loggedUserId, @Param("status") VacationStatus status);

    @Query("select v from Vacation v where v.createdBy.id in :userIds and v.dateFrom <= :dateTo and v.dateTo >= :dateFrom and v.status = :status")
    List<Vacation> getHistoryForUsers(@Param("dateFrom") ZonedDateTime dateFrom, @Param("dateTo") ZonedDateTime dateTo, @Param("userIds") List<Long> userIds, @Param("status") VacationStatus status);

    @Query("select v from Vacation v join fetch v.createdBy u left join fetch u.group " +
        "where v.status in :statuses and v.dateFrom <= :dayEnd and v.dateTo >= :dayStart and u.activated = true")
    List<Vacation> findAllCoveringDay(@Param("dayStart") ZonedDateTime dayStart, @Param("dayEnd") ZonedDateTime dayEnd, @Param("statuses") List<VacationStatus> statuses);

    @Query("select max(v) from Vacation v where v.createdBy.id = :userId")
    Vacation findLatest(@Param("userId") Long userId);

    @Query("select v.id from Vacation v where " +
        "v.createdBy.id in(:userIds) and (((v.status in('ACCEPTED_INITIALLY') or v.status in('ACCEPTED_INITIALLY','ACCEPTED','REJECTED') and v.createdBy.approvalVerification = 'TWO_LEVEL')) " +
        "or v.createdBy.approvalVerification = 'ONE_LEVEL')")
    List<Long> findIdsWithStatusVacationWaitingAndTwoLevelAndInitialApproval(@Param("userIds") List<Long> userIds);
}
