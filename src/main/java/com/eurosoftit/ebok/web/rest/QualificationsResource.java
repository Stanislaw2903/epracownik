package com.eurosoftit.ebok.web.rest;

import com.eurosoftit.ebok.domain.Qualifications;
import com.eurosoftit.ebok.service.QualificationsService;
import com.eurosoftit.ebok.web.rest.errors.BadRequestAlertException;
import com.eurosoftit.ebok.service.dto.QualificationsCriteria;
import com.eurosoftit.ebok.service.QualificationsQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.eurosoftit.ebok.domain.Qualifications}.
 */
@RestController
@RequestMapping("/api")
public class QualificationsResource {

    private final Logger log = LoggerFactory.getLogger(QualificationsResource.class);

    private static final String ENTITY_NAME = "qualifications";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final QualificationsService qualificationsService;

    private final QualificationsQueryService qualificationsQueryService;

    public QualificationsResource(QualificationsService qualificationsService, QualificationsQueryService qualificationsQueryService) {
        this.qualificationsService = qualificationsService;
        this.qualificationsQueryService = qualificationsQueryService;
    }

    /**
     * {@code POST  /qualifications} : Create a new qualifications.
     *
     * @param qualifications the qualifications to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new qualifications, or with status {@code 400 (Bad Request)} if the qualifications has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/qualifications")
    public ResponseEntity<Qualifications> createQualifications(@RequestBody Qualifications qualifications) throws URISyntaxException {
        log.debug("REST request to save Qualifications : {}", qualifications);
        if (qualifications.getId() != null) {
            throw new BadRequestAlertException("A new qualifications cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Qualifications result = qualificationsService.save(qualifications);
        return ResponseEntity.created(new URI("/api/qualifications/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /qualifications} : Updates an existing qualifications.
     *
     * @param qualifications the qualifications to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated qualifications,
     * or with status {@code 400 (Bad Request)} if the qualifications is not valid,
     * or with status {@code 500 (Internal Server Error)} if the qualifications couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/qualifications")
    public ResponseEntity<Qualifications> updateQualifications(@RequestBody Qualifications qualifications) throws URISyntaxException {
        log.debug("REST request to update Qualifications : {}", qualifications);
        if (qualifications.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Qualifications result = qualificationsService.save(qualifications);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, qualifications.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /qualifications} : get all the qualifications.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of qualifications in body.
     */
    @GetMapping("/qualifications")
    public ResponseEntity<List<Qualifications>> getAllQualifications(QualificationsCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Qualifications by criteria: {}", criteria);
        Page<Qualifications> page = qualificationsQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /qualifications/count} : count all the qualifications.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/qualifications/count")
    public ResponseEntity<Long> countQualifications(QualificationsCriteria criteria) {
        log.debug("REST request to count Qualifications by criteria: {}", criteria);
        return ResponseEntity.ok().body(qualificationsQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /qualifications/:id} : get the "id" qualifications.
     *
     * @param id the id of the qualifications to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the qualifications, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/qualifications/{id}")
    public ResponseEntity<Qualifications> getQualifications(@PathVariable Long id) {
        log.debug("REST request to get Qualifications : {}", id);
        Optional<Qualifications> qualifications = qualificationsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(qualifications);
    }

    /**
     * {@code DELETE  /qualifications/:id} : delete the "id" qualifications.
     *
     * @param id the id of the qualifications to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/qualifications/{id}")
    public ResponseEntity<Void> deleteQualifications(@PathVariable Long id) {
        log.debug("REST request to delete Qualifications : {}", id);
        qualificationsService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
