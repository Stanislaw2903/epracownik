package com.eurosoftit.ebok.service;

import com.eurosoftit.ebok.domain.Qualifications;
import com.eurosoftit.ebok.repository.QualificationsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Qualifications}.
 */
@Service
@Transactional
public class QualificationsService {

    private final Logger log = LoggerFactory.getLogger(QualificationsService.class);

    private final QualificationsRepository qualificationsRepository;

    public QualificationsService(QualificationsRepository qualificationsRepository) {
        this.qualificationsRepository = qualificationsRepository;
    }

    /**
     * Save a qualifications.
     *
     * @param qualifications the entity to save.
     * @return the persisted entity.
     */
    public Qualifications save(Qualifications qualifications) {
        log.debug("Request to save Qualifications : {}", qualifications);
        return qualificationsRepository.save(qualifications);
    }

    /**
     * Get all the qualifications.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Qualifications> findAll(Pageable pageable) {
        log.debug("Request to get all Qualifications");
        return qualificationsRepository.findAll(pageable);
    }


    /**
     * Get one qualifications by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Qualifications> findOne(Long id) {
        log.debug("Request to get Qualifications : {}", id);
        return qualificationsRepository.findById(id);
    }

    /**
     * Delete the qualifications by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Qualifications : {}", id);
        qualificationsRepository.deleteById(id);
    }
}
