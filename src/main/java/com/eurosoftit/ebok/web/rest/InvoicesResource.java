package com.eurosoftit.ebok.web.rest;

import com.eurosoftit.ebok.domain.Invoices;
import com.eurosoftit.ebok.repository.InvoicesRepository;
import com.eurosoftit.ebok.web.rest.errors.BadRequestAlertException;

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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.eurosoftit.ebok.domain.Invoices}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class InvoicesResource {

    private final Logger log = LoggerFactory.getLogger(InvoicesResource.class);

    private static final String ENTITY_NAME = "invoices";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InvoicesRepository invoicesRepository;

    public InvoicesResource(InvoicesRepository invoicesRepository) {
        this.invoicesRepository = invoicesRepository;
    }

    /**
     * {@code POST  /invoices} : Create a new invoices.
     *
     * @param invoices the invoices to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new invoices, or with status {@code 400 (Bad Request)} if the invoices has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/invoices")
    public ResponseEntity<Invoices> createInvoices(@RequestBody Invoices invoices) throws URISyntaxException {
        log.debug("REST request to save Invoices : {}", invoices);
        if (invoices.getId() != null) {
            throw new BadRequestAlertException("A new invoices cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Invoices result = invoicesRepository.save(invoices);
        return ResponseEntity.created(new URI("/api/invoices/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /invoices} : Updates an existing invoices.
     *
     * @param invoices the invoices to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated invoices,
     * or with status {@code 400 (Bad Request)} if the invoices is not valid,
     * or with status {@code 500 (Internal Server Error)} if the invoices couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/invoices")
    public ResponseEntity<Invoices> updateInvoices(@RequestBody Invoices invoices) throws URISyntaxException {
        log.debug("REST request to update Invoices : {}", invoices);
        if (invoices.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Invoices result = invoicesRepository.save(invoices);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, invoices.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /invoices} : get all the invoices.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of invoices in body.
     */
    @GetMapping("/invoices")
    public ResponseEntity<List<Invoices>> getAllInvoices(Pageable pageable) {
        log.debug("REST request to get a page of Invoices");
        Page<Invoices> page = invoicesRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /invoices/:id} : get the "id" invoices.
     *
     * @param id the id of the invoices to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the invoices, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/invoices/{id}")
    public ResponseEntity<Invoices> getInvoices(@PathVariable Long id) {
        log.debug("REST request to get Invoices : {}", id);
        Optional<Invoices> invoices = invoicesRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(invoices);
    }

    /**
     * {@code DELETE  /invoices/:id} : delete the "id" invoices.
     *
     * @param id the id of the invoices to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/invoices/{id}")
    public ResponseEntity<Void> deleteInvoices(@PathVariable Long id) {
        log.debug("REST request to delete Invoices : {}", id);
        invoicesRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
