package com.eurosoftit.ebok.web.rest.graphic;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;

public interface GraphicAbsenceViewRepository extends Repository<GraphicAbsenceView, Long> {
    @Query("select v from GraphicAbsenceView v where v.externalId = :externalId and v.dateFrom <= :end and v.dateTo >= :start")
    List<GraphicAbsenceView> getAbsencesByRange(@Param("start") ZonedDateTime start, @Param("end") ZonedDateTime end, @Param("externalId") Long externalId);

    @Query("select v from GraphicAbsenceView v where v.externalId in :externalIds and v.dateFrom <= :end and v.dateTo >= :start")
    List<GraphicAbsenceView> getAbsencesByRangeForUsers(@Param("start") ZonedDateTime start, @Param("end") ZonedDateTime end, @Param("externalIds") List<Long> externalIds);
}
