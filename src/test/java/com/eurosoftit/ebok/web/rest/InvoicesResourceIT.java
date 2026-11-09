package com.eurosoftit.ebok.web.rest;

import com.eurosoftit.ebok.EbokApp;
import com.eurosoftit.ebok.domain.Invoices;
import com.eurosoftit.ebok.repository.InvoicesRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link InvoicesResource} REST controller.
 */
@SpringBootTest(classes = EbokApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class InvoicesResourceIT {

    private static final LocalDate DEFAULT_ISSUE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ISSUE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_DOCUMENT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_DOCUMENT_NUMBER = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_PAYMENT_DEADLINE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PAYMENT_DEADLINE = LocalDate.now(ZoneId.systemDefault());

    private static final Float DEFAULT_GROSS_AMOUNT = 1F;
    private static final Float UPDATED_GROSS_AMOUNT = 2F;

    private static final Float DEFAULT_TO_PAY = 1F;
    private static final Float UPDATED_TO_PAY = 2F;

    private static final byte[] DEFAULT_DOCUMENT_FILE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_DOCUMENT_FILE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_DOCUMENT_FILE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_DOCUMENT_FILE_CONTENT_TYPE = "image/png";

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    @Autowired
    private InvoicesRepository invoicesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInvoicesMockMvc;

    private Invoices invoices;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Invoices createEntity(EntityManager em) {
        Invoices invoices = new Invoices()
            .issueDate(DEFAULT_ISSUE_DATE)
            .documentNumber(DEFAULT_DOCUMENT_NUMBER)
            .paymentDeadline(DEFAULT_PAYMENT_DEADLINE)
            .grossAmount(DEFAULT_GROSS_AMOUNT)
            .toPay(DEFAULT_TO_PAY)
            .documentFile(DEFAULT_DOCUMENT_FILE)
            .documentFileContentType(DEFAULT_DOCUMENT_FILE_CONTENT_TYPE)
            .status(DEFAULT_STATUS);
        return invoices;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Invoices createUpdatedEntity(EntityManager em) {
        Invoices invoices = new Invoices()
            .issueDate(UPDATED_ISSUE_DATE)
            .documentNumber(UPDATED_DOCUMENT_NUMBER)
            .paymentDeadline(UPDATED_PAYMENT_DEADLINE)
            .grossAmount(UPDATED_GROSS_AMOUNT)
            .toPay(UPDATED_TO_PAY)
            .documentFile(UPDATED_DOCUMENT_FILE)
            .documentFileContentType(UPDATED_DOCUMENT_FILE_CONTENT_TYPE)
            .status(UPDATED_STATUS);
        return invoices;
    }

    @BeforeEach
    public void initTest() {
        invoices = createEntity(em);
    }

    @Test
    @Transactional
    public void createInvoices() throws Exception {
        int databaseSizeBeforeCreate = invoicesRepository.findAll().size();
        // Create the Invoices
        restInvoicesMockMvc.perform(post("/api/invoices")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(invoices)))
            .andExpect(status().isCreated());

        // Validate the Invoices in the database
        List<Invoices> invoicesList = invoicesRepository.findAll();
        assertThat(invoicesList).hasSize(databaseSizeBeforeCreate + 1);
        Invoices testInvoices = invoicesList.get(invoicesList.size() - 1);
        assertThat(testInvoices.getIssueDate()).isEqualTo(DEFAULT_ISSUE_DATE);
        assertThat(testInvoices.getDocumentNumber()).isEqualTo(DEFAULT_DOCUMENT_NUMBER);
        assertThat(testInvoices.getPaymentDeadline()).isEqualTo(DEFAULT_PAYMENT_DEADLINE);
        assertThat(testInvoices.getGrossAmount()).isEqualTo(DEFAULT_GROSS_AMOUNT);
        assertThat(testInvoices.getToPay()).isEqualTo(DEFAULT_TO_PAY);
        assertThat(testInvoices.getDocumentFile()).isEqualTo(DEFAULT_DOCUMENT_FILE);
        assertThat(testInvoices.getDocumentFileContentType()).isEqualTo(DEFAULT_DOCUMENT_FILE_CONTENT_TYPE);
        assertThat(testInvoices.isStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    public void createInvoicesWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = invoicesRepository.findAll().size();

        // Create the Invoices with an existing ID
        invoices.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restInvoicesMockMvc.perform(post("/api/invoices")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(invoices)))
            .andExpect(status().isBadRequest());

        // Validate the Invoices in the database
        List<Invoices> invoicesList = invoicesRepository.findAll();
        assertThat(invoicesList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllInvoices() throws Exception {
        // Initialize the database
        invoicesRepository.saveAndFlush(invoices);

        // Get all the invoicesList
        restInvoicesMockMvc.perform(get("/api/invoices?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(invoices.getId().intValue())))
            .andExpect(jsonPath("$.[*].issueDate").value(hasItem(DEFAULT_ISSUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].documentNumber").value(hasItem(DEFAULT_DOCUMENT_NUMBER)))
            .andExpect(jsonPath("$.[*].paymentDeadline").value(hasItem(DEFAULT_PAYMENT_DEADLINE.toString())))
            .andExpect(jsonPath("$.[*].grossAmount").value(hasItem(DEFAULT_GROSS_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].toPay").value(hasItem(DEFAULT_TO_PAY.doubleValue())))
            .andExpect(jsonPath("$.[*].documentFileContentType").value(hasItem(DEFAULT_DOCUMENT_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].documentFile").value(hasItem(Base64Utils.encodeToString(DEFAULT_DOCUMENT_FILE))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getInvoices() throws Exception {
        // Initialize the database
        invoicesRepository.saveAndFlush(invoices);

        // Get the invoices
        restInvoicesMockMvc.perform(get("/api/invoices/{id}", invoices.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(invoices.getId().intValue()))
            .andExpect(jsonPath("$.issueDate").value(DEFAULT_ISSUE_DATE.toString()))
            .andExpect(jsonPath("$.documentNumber").value(DEFAULT_DOCUMENT_NUMBER))
            .andExpect(jsonPath("$.paymentDeadline").value(DEFAULT_PAYMENT_DEADLINE.toString()))
            .andExpect(jsonPath("$.grossAmount").value(DEFAULT_GROSS_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.toPay").value(DEFAULT_TO_PAY.doubleValue()))
            .andExpect(jsonPath("$.documentFileContentType").value(DEFAULT_DOCUMENT_FILE_CONTENT_TYPE))
            .andExpect(jsonPath("$.documentFile").value(Base64Utils.encodeToString(DEFAULT_DOCUMENT_FILE)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.booleanValue()));
    }
    @Test
    @Transactional
    public void getNonExistingInvoices() throws Exception {
        // Get the invoices
        restInvoicesMockMvc.perform(get("/api/invoices/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateInvoices() throws Exception {
        // Initialize the database
        invoicesRepository.saveAndFlush(invoices);

        int databaseSizeBeforeUpdate = invoicesRepository.findAll().size();

        // Update the invoices
        Invoices updatedInvoices = invoicesRepository.findById(invoices.getId()).get();
        // Disconnect from session so that the updates on updatedInvoices are not directly saved in db
        em.detach(updatedInvoices);
        updatedInvoices
            .issueDate(UPDATED_ISSUE_DATE)
            .documentNumber(UPDATED_DOCUMENT_NUMBER)
            .paymentDeadline(UPDATED_PAYMENT_DEADLINE)
            .grossAmount(UPDATED_GROSS_AMOUNT)
            .toPay(UPDATED_TO_PAY)
            .documentFile(UPDATED_DOCUMENT_FILE)
            .documentFileContentType(UPDATED_DOCUMENT_FILE_CONTENT_TYPE)
            .status(UPDATED_STATUS);

        restInvoicesMockMvc.perform(put("/api/invoices")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedInvoices)))
            .andExpect(status().isOk());

        // Validate the Invoices in the database
        List<Invoices> invoicesList = invoicesRepository.findAll();
        assertThat(invoicesList).hasSize(databaseSizeBeforeUpdate);
        Invoices testInvoices = invoicesList.get(invoicesList.size() - 1);
        assertThat(testInvoices.getIssueDate()).isEqualTo(UPDATED_ISSUE_DATE);
        assertThat(testInvoices.getDocumentNumber()).isEqualTo(UPDATED_DOCUMENT_NUMBER);
        assertThat(testInvoices.getPaymentDeadline()).isEqualTo(UPDATED_PAYMENT_DEADLINE);
        assertThat(testInvoices.getGrossAmount()).isEqualTo(UPDATED_GROSS_AMOUNT);
        assertThat(testInvoices.getToPay()).isEqualTo(UPDATED_TO_PAY);
        assertThat(testInvoices.getDocumentFile()).isEqualTo(UPDATED_DOCUMENT_FILE);
        assertThat(testInvoices.getDocumentFileContentType()).isEqualTo(UPDATED_DOCUMENT_FILE_CONTENT_TYPE);
        assertThat(testInvoices.isStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void updateNonExistingInvoices() throws Exception {
        int databaseSizeBeforeUpdate = invoicesRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInvoicesMockMvc.perform(put("/api/invoices")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(invoices)))
            .andExpect(status().isBadRequest());

        // Validate the Invoices in the database
        List<Invoices> invoicesList = invoicesRepository.findAll();
        assertThat(invoicesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteInvoices() throws Exception {
        // Initialize the database
        invoicesRepository.saveAndFlush(invoices);

        int databaseSizeBeforeDelete = invoicesRepository.findAll().size();

        // Delete the invoices
        restInvoicesMockMvc.perform(delete("/api/invoices/{id}", invoices.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Invoices> invoicesList = invoicesRepository.findAll();
        assertThat(invoicesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
