package com.eurosoftit.ebok.service;

import com.eurosoftit.ebok.domain.NotificationRead;
import com.eurosoftit.ebok.repository.NotificationReadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link NotificationRead}.
 */
@Service
@Transactional
public class NotificationReadService {

    private final Logger log = LoggerFactory.getLogger(NotificationReadService.class);

    private final NotificationReadRepository notificationReadRepository;

    public NotificationReadService(NotificationReadRepository notificationReadRepository) {
        this.notificationReadRepository = notificationReadRepository;
    }

    /**
     * Save a notificationRead.
     *
     * @param notificationRead the entity to save.
     * @return the persisted entity.
     */
    public NotificationRead save(NotificationRead notificationRead) {
        log.debug("Request to save NotificationRead : {}", notificationRead);
        return notificationReadRepository.save(notificationRead);
    }

    /**
     * Get all the notificationReads.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<NotificationRead> findAll() {
        log.debug("Request to get all NotificationReads");
        return notificationReadRepository.findAllWithEagerRelationships();
    }


    /**
     * Get all the notificationReads with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<NotificationRead> findAllWithEagerRelationships(Pageable pageable) {
        return notificationReadRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one notificationRead by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<NotificationRead> findOne(Long id) {
        log.debug("Request to get NotificationRead : {}", id);
        return notificationReadRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the notificationRead by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete NotificationRead : {}", id);
        notificationReadRepository.deleteById(id);
    }
}
