package com.eurosoftit.ebok.web.rest;

import com.eurosoftit.ebok.domain.NotificationRead;
import com.eurosoftit.ebok.domain.User;
import com.eurosoftit.ebok.repository.NotificationReadRepository;
import com.eurosoftit.ebok.repository.UserRepository;
import com.eurosoftit.ebok.security.SecurityUtils;
import com.eurosoftit.ebok.service.NotificationReadService;
import com.eurosoftit.ebok.web.rest.errors.BadRequestAlertException;
import com.eurosoftit.ebok.service.dto.NotificationReadCriteria;
import com.eurosoftit.ebok.service.NotificationReadQueryService;

import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.eurosoftit.ebok.domain.NotificationRead}.
 */
@RestController
@RequestMapping("/api")
public class NotificationReadResource {

    private static class NotificationResourceException extends RuntimeException {
        private NotificationResourceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(NotificationReadResource.class);

    private static final String ENTITY_NAME = "notificationRead";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NotificationReadService notificationReadService;
    private final NotificationReadRepository notificationReadRepository;
    private UserRepository userRepository;

    private final NotificationReadQueryService notificationReadQueryService;

    public NotificationReadResource(
        NotificationReadService notificationReadService,
        NotificationReadQueryService notificationReadQueryService,
        NotificationReadRepository notificationReadRepository,
        UserRepository userRepository) {
        this.notificationReadService = notificationReadService;
        this.userRepository = userRepository;
        this.notificationReadRepository = notificationReadRepository;
        this.notificationReadQueryService = notificationReadQueryService;
    }

    /**
     * {@code POST  /notification-reads} : Create a new notificationRead.
     *
     * @param notificationRead the notificationRead to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new notificationRead, or with status {@code 400 (Bad Request)} if the notificationRead has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/notification-reads")
    public ResponseEntity<NotificationRead> createNotificationRead(@RequestBody NotificationRead notificationRead) throws URISyntaxException {
        log.debug("REST request to save NotificationRead : {}", notificationRead);
        if (notificationRead.getId() != null) {
            throw new BadRequestAlertException("A new notificationRead cannot already have an ID", ENTITY_NAME, "idexists");
        }
        NotificationRead result = notificationReadService.save(notificationRead);
        return ResponseEntity.created(new URI("/api/notification-reads/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /notification-reads} : Updates an existing notificationRead.
     *
     * @param notificationRead the notificationRead to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationRead,
     * or with status {@code 400 (Bad Request)} if the notificationRead is not valid,
     * or with status {@code 500 (Internal Server Error)} if the notificationRead couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/notification-reads")
    public ResponseEntity<NotificationRead> updateNotificationRead(@RequestBody NotificationRead notificationRead) throws URISyntaxException {
        log.debug("REST request to update NotificationRead : {}", notificationRead);
        if (notificationRead.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        NotificationRead result = notificationReadService.save(notificationRead);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notificationRead.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /notification-reads} : get all the notificationReads.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notificationReads in body.
     */
    @GetMapping("/notification-reads")
    public ResponseEntity<List<NotificationRead>> getAllNotificationReads(NotificationReadCriteria criteria) {
        log.debug("REST request to get NotificationReads by criteria: {}", criteria);
        List<NotificationRead> entityList = notificationReadQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /notification-reads/count} : count all the notificationReads.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/notification-reads/count")
    public ResponseEntity<Long> countNotificationReads(NotificationReadCriteria criteria) {
        log.debug("REST request to count NotificationReads by criteria: {}", criteria);
        return ResponseEntity.ok().body(notificationReadQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /notification-reads/:id} : get the "id" notificationRead.
     *
     * @param id the id of the notificationRead to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notificationRead, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/notification-reads/{id}")
    public ResponseEntity<NotificationRead> getNotificationRead(@PathVariable Long id) {
        log.debug("REST request to get NotificationRead : {}", id);
        Optional<NotificationRead> notificationRead = notificationReadService.findOne(id);
        return ResponseUtil.wrapOrNotFound(notificationRead);
    }

    /**
     * {@code GET  /notification-reads/notification/:id} : get the "id" notificationRead.
     *
     * @param id the id of the notificationRead to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notificationRead, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/notification-reads/notification/{id}")
    public ResponseEntity <List<NotificationRead>> getNotificationReadByNotificationId(@PathVariable Long id) {
        log.debug("----REST request to get NotificationReads: {}", id);
        List<NotificationRead> entityList = notificationReadRepository.findAllByNotificationIdWithEagerRelationships(id);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code DELETE  /notification-reads/:id} : delete the "id" notificationRead.
     *
     * @param id the id of the notificationRead to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/notification-reads/{id}")
    public ResponseEntity<Void> deleteNotificationRead(@PathVariable Long id) {
        log.debug("REST request to delete NotificationRead : {}", id);
        notificationReadService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    private User GetUser(){
        String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new NotificationReadResource.NotificationResourceException("Current user login not found"));

        Optional<User> user = userRepository.findOneByLogin(userLogin);
        if (!user.isPresent()) {
            throw new NotificationReadResource.NotificationResourceException("User could not be found");
        }
        return user.get();
    }

    private IntegerFilter ConvertToIntegerFilter(Long value){
        Long longValue = value;
        IntegerFilter longFilter = new IntegerFilter();
        longFilter.setEquals(longValue.intValue());
        return longFilter;
    }
}
