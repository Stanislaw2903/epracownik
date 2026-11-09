package com.eurosoftit.ebok.web.rest.vacation;

import com.eurosoftit.ebok.security.AuthoritiesConstants;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@Api
@RestController
@RequestMapping("/api/vacation/history/")
class VacationHistoryResource {
    private final Logger log = LoggerFactory.getLogger(VacationHistoryResource.class);
    private static final String ENTITY_NAME = "vacation";

    @Autowired
    private VacationService vacationService;

    @GetMapping
    Collection<Long> get(@RequestParam("year") Integer year){
        log.debug("REST request to get vacation history");
        return vacationService.getHistoryByYearForUser(year);
    }

    @GetMapping("/details")
    Collection<VacationDetailsHistoryDTO> details(@RequestParam("year") Integer year){
        log.debug("REST request to get vacation history details");
        return vacationService.getHistoryDetailsByYearForUser(year);
    }

    @GetMapping("/details/{externalId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")" + " OR " + "hasAuthority(\"" + AuthoritiesConstants.SYSTEM + "\")" + " OR " + "hasAuthority(\"" + AuthoritiesConstants.HR + "\")")
    Collection<VacationDetailsHistoryDTO> detailsForUser(@PathVariable("externalId") Long externalId, @RequestParam("year") Integer year){
        log.debug("REST request to get vacation history details for external id {}", externalId);
        return vacationService.getHistoryDetailsByYearForExternalId(year, externalId);
    }
}
