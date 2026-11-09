package com.eurosoftit.ebok.web.rest;

import com.eurosoftit.ebok.EbokApp;
import com.eurosoftit.ebok.domain.Qualifications;
import com.eurosoftit.ebok.repository.QualificationsRepository;
import com.eurosoftit.ebok.service.QualificationsService;
import com.eurosoftit.ebok.service.dto.QualificationsCriteria;
import com.eurosoftit.ebok.service.QualificationsQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link QualificationsResource} REST controller.
 */
@SpringBootTest(classes = EbokApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class QualificationsResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_START_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_EXPIRE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EXPIRE_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_EXPIRE_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_COMMENTS = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTS = "BBBBBBBBBB";

    private static final Boolean DEFAULT_SUBSIDIZED = false;
    private static final Boolean UPDATED_SUBSIDIZED = true;

    @Autowired
    private QualificationsRepository qualificationsRepository;

    @Autowired
    private QualificationsService qualificationsService;

    @Autowired
    private QualificationsQueryService qualificationsQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restQualificationsMockMvc;

    private Qualifications qualifications;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Qualifications createEntity(EntityManager em) {
        Qualifications qualifications = new Qualifications()
            .name(DEFAULT_NAME)
            .startDate(DEFAULT_START_DATE)
            .expireDate(DEFAULT_EXPIRE_DATE)
            .comments(DEFAULT_COMMENTS)
            .subsidized(DEFAULT_SUBSIDIZED);
        return qualifications;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Qualifications createUpdatedEntity(EntityManager em) {
        Qualifications qualifications = new Qualifications()
            .name(UPDATED_NAME)
            .startDate(UPDATED_START_DATE)
            .expireDate(UPDATED_EXPIRE_DATE)
            .comments(UPDATED_COMMENTS)
            .subsidized(UPDATED_SUBSIDIZED);
        return qualifications;
    }

    @BeforeEach
    public void initTest() {
        qualifications = createEntity(em);
    }

    @Test
    @Transactional
    public void createQualifications() throws Exception {
        int databaseSizeBeforeCreate = qualificationsRepository.findAll().size();
        // Create the Qualifications
        restQualificationsMockMvc.perform(post("/api/qualifications")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(qualifications)))
            .andExpect(status().isCreated());

        // Validate the Qualifications in the database
        List<Qualifications> qualificationsList = qualificationsRepository.findAll();
        assertThat(qualificationsList).hasSize(databaseSizeBeforeCreate + 1);
        Qualifications testQualifications = qualificationsList.get(qualificationsList.size() - 1);
        assertThat(testQualifications.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testQualifications.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testQualifications.getExpireDate()).isEqualTo(DEFAULT_EXPIRE_DATE);
        assertThat(testQualifications.getComments()).isEqualTo(DEFAULT_COMMENTS);
        assertThat(testQualifications.isSubsidized()).isEqualTo(DEFAULT_SUBSIDIZED);
    }

    @Test
    @Transactional
    public void createQualificationsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = qualificationsRepository.findAll().size();

        // Create the Qualifications with an existing ID
        qualifications.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restQualificationsMockMvc.perform(post("/api/qualifications")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(qualifications)))
            .andExpect(status().isBadRequest());

        // Validate the Qualifications in the database
        List<Qualifications> qualificationsList = qualificationsRepository.findAll();
        assertThat(qualificationsList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllQualifications() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList
        restQualificationsMockMvc.perform(get("/api/qualifications?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(qualifications.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].expireDate").value(hasItem(DEFAULT_EXPIRE_DATE.toString())))
            .andExpect(jsonPath("$.[*].comments").value(hasItem(DEFAULT_COMMENTS)))
            .andExpect(jsonPath("$.[*].subsidized").value(hasItem(DEFAULT_SUBSIDIZED.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getQualifications() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get the qualifications
        restQualificationsMockMvc.perform(get("/api/qualifications/{id}", qualifications.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(qualifications.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.expireDate").value(DEFAULT_EXPIRE_DATE.toString()))
            .andExpect(jsonPath("$.comments").value(DEFAULT_COMMENTS))
            .andExpect(jsonPath("$.subsidized").value(DEFAULT_SUBSIDIZED.booleanValue()));
    }


    @Test
    @Transactional
    public void getQualificationsByIdFiltering() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        Long id = qualifications.getId();

        defaultQualificationsShouldBeFound("id.equals=" + id);
        defaultQualificationsShouldNotBeFound("id.notEquals=" + id);

        defaultQualificationsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultQualificationsShouldNotBeFound("id.greaterThan=" + id);

        defaultQualificationsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultQualificationsShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllQualificationsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where name equals to DEFAULT_NAME
        defaultQualificationsShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the qualificationsList where name equals to UPDATED_NAME
        defaultQualificationsShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllQualificationsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where name not equals to DEFAULT_NAME
        defaultQualificationsShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the qualificationsList where name not equals to UPDATED_NAME
        defaultQualificationsShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllQualificationsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where name in DEFAULT_NAME or UPDATED_NAME
        defaultQualificationsShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the qualificationsList where name equals to UPDATED_NAME
        defaultQualificationsShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllQualificationsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where name is not null
        defaultQualificationsShouldBeFound("name.specified=true");

        // Get all the qualificationsList where name is null
        defaultQualificationsShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllQualificationsByNameContainsSomething() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where name contains DEFAULT_NAME
        defaultQualificationsShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the qualificationsList where name contains UPDATED_NAME
        defaultQualificationsShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllQualificationsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where name does not contain DEFAULT_NAME
        defaultQualificationsShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the qualificationsList where name does not contain UPDATED_NAME
        defaultQualificationsShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllQualificationsByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where startDate equals to DEFAULT_START_DATE
        defaultQualificationsShouldBeFound("startDate.equals=" + DEFAULT_START_DATE);

        // Get all the qualificationsList where startDate equals to UPDATED_START_DATE
        defaultQualificationsShouldNotBeFound("startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllQualificationsByStartDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where startDate not equals to DEFAULT_START_DATE
        defaultQualificationsShouldNotBeFound("startDate.notEquals=" + DEFAULT_START_DATE);

        // Get all the qualificationsList where startDate not equals to UPDATED_START_DATE
        defaultQualificationsShouldBeFound("startDate.notEquals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllQualificationsByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where startDate in DEFAULT_START_DATE or UPDATED_START_DATE
        defaultQualificationsShouldBeFound("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE);

        // Get all the qualificationsList where startDate equals to UPDATED_START_DATE
        defaultQualificationsShouldNotBeFound("startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllQualificationsByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where startDate is not null
        defaultQualificationsShouldBeFound("startDate.specified=true");

        // Get all the qualificationsList where startDate is null
        defaultQualificationsShouldNotBeFound("startDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllQualificationsByStartDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where startDate is greater than or equal to DEFAULT_START_DATE
        defaultQualificationsShouldBeFound("startDate.greaterThanOrEqual=" + DEFAULT_START_DATE);

        // Get all the qualificationsList where startDate is greater than or equal to UPDATED_START_DATE
        defaultQualificationsShouldNotBeFound("startDate.greaterThanOrEqual=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllQualificationsByStartDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where startDate is less than or equal to DEFAULT_START_DATE
        defaultQualificationsShouldBeFound("startDate.lessThanOrEqual=" + DEFAULT_START_DATE);

        // Get all the qualificationsList where startDate is less than or equal to SMALLER_START_DATE
        defaultQualificationsShouldNotBeFound("startDate.lessThanOrEqual=" + SMALLER_START_DATE);
    }

    @Test
    @Transactional
    public void getAllQualificationsByStartDateIsLessThanSomething() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where startDate is less than DEFAULT_START_DATE
        defaultQualificationsShouldNotBeFound("startDate.lessThan=" + DEFAULT_START_DATE);

        // Get all the qualificationsList where startDate is less than UPDATED_START_DATE
        defaultQualificationsShouldBeFound("startDate.lessThan=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllQualificationsByStartDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where startDate is greater than DEFAULT_START_DATE
        defaultQualificationsShouldNotBeFound("startDate.greaterThan=" + DEFAULT_START_DATE);

        // Get all the qualificationsList where startDate is greater than SMALLER_START_DATE
        defaultQualificationsShouldBeFound("startDate.greaterThan=" + SMALLER_START_DATE);
    }


    @Test
    @Transactional
    public void getAllQualificationsByExpireDateIsEqualToSomething() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where expireDate equals to DEFAULT_EXPIRE_DATE
        defaultQualificationsShouldBeFound("expireDate.equals=" + DEFAULT_EXPIRE_DATE);

        // Get all the qualificationsList where expireDate equals to UPDATED_EXPIRE_DATE
        defaultQualificationsShouldNotBeFound("expireDate.equals=" + UPDATED_EXPIRE_DATE);
    }

    @Test
    @Transactional
    public void getAllQualificationsByExpireDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where expireDate not equals to DEFAULT_EXPIRE_DATE
        defaultQualificationsShouldNotBeFound("expireDate.notEquals=" + DEFAULT_EXPIRE_DATE);

        // Get all the qualificationsList where expireDate not equals to UPDATED_EXPIRE_DATE
        defaultQualificationsShouldBeFound("expireDate.notEquals=" + UPDATED_EXPIRE_DATE);
    }

    @Test
    @Transactional
    public void getAllQualificationsByExpireDateIsInShouldWork() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where expireDate in DEFAULT_EXPIRE_DATE or UPDATED_EXPIRE_DATE
        defaultQualificationsShouldBeFound("expireDate.in=" + DEFAULT_EXPIRE_DATE + "," + UPDATED_EXPIRE_DATE);

        // Get all the qualificationsList where expireDate equals to UPDATED_EXPIRE_DATE
        defaultQualificationsShouldNotBeFound("expireDate.in=" + UPDATED_EXPIRE_DATE);
    }

    @Test
    @Transactional
    public void getAllQualificationsByExpireDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where expireDate is not null
        defaultQualificationsShouldBeFound("expireDate.specified=true");

        // Get all the qualificationsList where expireDate is null
        defaultQualificationsShouldNotBeFound("expireDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllQualificationsByExpireDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where expireDate is greater than or equal to DEFAULT_EXPIRE_DATE
        defaultQualificationsShouldBeFound("expireDate.greaterThanOrEqual=" + DEFAULT_EXPIRE_DATE);

        // Get all the qualificationsList where expireDate is greater than or equal to UPDATED_EXPIRE_DATE
        defaultQualificationsShouldNotBeFound("expireDate.greaterThanOrEqual=" + UPDATED_EXPIRE_DATE);
    }

    @Test
    @Transactional
    public void getAllQualificationsByExpireDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where expireDate is less than or equal to DEFAULT_EXPIRE_DATE
        defaultQualificationsShouldBeFound("expireDate.lessThanOrEqual=" + DEFAULT_EXPIRE_DATE);

        // Get all the qualificationsList where expireDate is less than or equal to SMALLER_EXPIRE_DATE
        defaultQualificationsShouldNotBeFound("expireDate.lessThanOrEqual=" + SMALLER_EXPIRE_DATE);
    }

    @Test
    @Transactional
    public void getAllQualificationsByExpireDateIsLessThanSomething() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where expireDate is less than DEFAULT_EXPIRE_DATE
        defaultQualificationsShouldNotBeFound("expireDate.lessThan=" + DEFAULT_EXPIRE_DATE);

        // Get all the qualificationsList where expireDate is less than UPDATED_EXPIRE_DATE
        defaultQualificationsShouldBeFound("expireDate.lessThan=" + UPDATED_EXPIRE_DATE);
    }

    @Test
    @Transactional
    public void getAllQualificationsByExpireDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where expireDate is greater than DEFAULT_EXPIRE_DATE
        defaultQualificationsShouldNotBeFound("expireDate.greaterThan=" + DEFAULT_EXPIRE_DATE);

        // Get all the qualificationsList where expireDate is greater than SMALLER_EXPIRE_DATE
        defaultQualificationsShouldBeFound("expireDate.greaterThan=" + SMALLER_EXPIRE_DATE);
    }


    @Test
    @Transactional
    public void getAllQualificationsByCommentsIsEqualToSomething() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where comments equals to DEFAULT_COMMENTS
        defaultQualificationsShouldBeFound("comments.equals=" + DEFAULT_COMMENTS);

        // Get all the qualificationsList where comments equals to UPDATED_COMMENTS
        defaultQualificationsShouldNotBeFound("comments.equals=" + UPDATED_COMMENTS);
    }

    @Test
    @Transactional
    public void getAllQualificationsByCommentsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where comments not equals to DEFAULT_COMMENTS
        defaultQualificationsShouldNotBeFound("comments.notEquals=" + DEFAULT_COMMENTS);

        // Get all the qualificationsList where comments not equals to UPDATED_COMMENTS
        defaultQualificationsShouldBeFound("comments.notEquals=" + UPDATED_COMMENTS);
    }

    @Test
    @Transactional
    public void getAllQualificationsByCommentsIsInShouldWork() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where comments in DEFAULT_COMMENTS or UPDATED_COMMENTS
        defaultQualificationsShouldBeFound("comments.in=" + DEFAULT_COMMENTS + "," + UPDATED_COMMENTS);

        // Get all the qualificationsList where comments equals to UPDATED_COMMENTS
        defaultQualificationsShouldNotBeFound("comments.in=" + UPDATED_COMMENTS);
    }

    @Test
    @Transactional
    public void getAllQualificationsByCommentsIsNullOrNotNull() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where comments is not null
        defaultQualificationsShouldBeFound("comments.specified=true");

        // Get all the qualificationsList where comments is null
        defaultQualificationsShouldNotBeFound("comments.specified=false");
    }
                @Test
    @Transactional
    public void getAllQualificationsByCommentsContainsSomething() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where comments contains DEFAULT_COMMENTS
        defaultQualificationsShouldBeFound("comments.contains=" + DEFAULT_COMMENTS);

        // Get all the qualificationsList where comments contains UPDATED_COMMENTS
        defaultQualificationsShouldNotBeFound("comments.contains=" + UPDATED_COMMENTS);
    }

    @Test
    @Transactional
    public void getAllQualificationsByCommentsNotContainsSomething() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where comments does not contain DEFAULT_COMMENTS
        defaultQualificationsShouldNotBeFound("comments.doesNotContain=" + DEFAULT_COMMENTS);

        // Get all the qualificationsList where comments does not contain UPDATED_COMMENTS
        defaultQualificationsShouldBeFound("comments.doesNotContain=" + UPDATED_COMMENTS);
    }


    @Test
    @Transactional
    public void getAllQualificationsBySubsidizedIsEqualToSomething() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where subsidized equals to DEFAULT_SUBSIDIZED
        defaultQualificationsShouldBeFound("subsidized.equals=" + DEFAULT_SUBSIDIZED);

        // Get all the qualificationsList where subsidized equals to UPDATED_SUBSIDIZED
        defaultQualificationsShouldNotBeFound("subsidized.equals=" + UPDATED_SUBSIDIZED);
    }

    @Test
    @Transactional
    public void getAllQualificationsBySubsidizedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where subsidized not equals to DEFAULT_SUBSIDIZED
        defaultQualificationsShouldNotBeFound("subsidized.notEquals=" + DEFAULT_SUBSIDIZED);

        // Get all the qualificationsList where subsidized not equals to UPDATED_SUBSIDIZED
        defaultQualificationsShouldBeFound("subsidized.notEquals=" + UPDATED_SUBSIDIZED);
    }

    @Test
    @Transactional
    public void getAllQualificationsBySubsidizedIsInShouldWork() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where subsidized in DEFAULT_SUBSIDIZED or UPDATED_SUBSIDIZED
        defaultQualificationsShouldBeFound("subsidized.in=" + DEFAULT_SUBSIDIZED + "," + UPDATED_SUBSIDIZED);

        // Get all the qualificationsList where subsidized equals to UPDATED_SUBSIDIZED
        defaultQualificationsShouldNotBeFound("subsidized.in=" + UPDATED_SUBSIDIZED);
    }

    @Test
    @Transactional
    public void getAllQualificationsBySubsidizedIsNullOrNotNull() throws Exception {
        // Initialize the database
        qualificationsRepository.saveAndFlush(qualifications);

        // Get all the qualificationsList where subsidized is not null
        defaultQualificationsShouldBeFound("subsidized.specified=true");

        // Get all the qualificationsList where subsidized is null
        defaultQualificationsShouldNotBeFound("subsidized.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultQualificationsShouldBeFound(String filter) throws Exception {
        restQualificationsMockMvc.perform(get("/api/qualifications?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(qualifications.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].expireDate").value(hasItem(DEFAULT_EXPIRE_DATE.toString())))
            .andExpect(jsonPath("$.[*].comments").value(hasItem(DEFAULT_COMMENTS)))
            .andExpect(jsonPath("$.[*].subsidized").value(hasItem(DEFAULT_SUBSIDIZED.booleanValue())));

        // Check, that the count call also returns 1
        restQualificationsMockMvc.perform(get("/api/qualifications/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultQualificationsShouldNotBeFound(String filter) throws Exception {
        restQualificationsMockMvc.perform(get("/api/qualifications?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restQualificationsMockMvc.perform(get("/api/qualifications/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingQualifications() throws Exception {
        // Get the qualifications
        restQualificationsMockMvc.perform(get("/api/qualifications/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateQualifications() throws Exception {
        // Initialize the database
        qualificationsService.save(qualifications);

        int databaseSizeBeforeUpdate = qualificationsRepository.findAll().size();

        // Update the qualifications
        Qualifications updatedQualifications = qualificationsRepository.findById(qualifications.getId()).get();
        // Disconnect from session so that the updates on updatedQualifications are not directly saved in db
        em.detach(updatedQualifications);
        updatedQualifications
            .name(UPDATED_NAME)
            .startDate(UPDATED_START_DATE)
            .expireDate(UPDATED_EXPIRE_DATE)
            .comments(UPDATED_COMMENTS)
            .subsidized(UPDATED_SUBSIDIZED);

        restQualificationsMockMvc.perform(put("/api/qualifications")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedQualifications)))
            .andExpect(status().isOk());

        // Validate the Qualifications in the database
        List<Qualifications> qualificationsList = qualificationsRepository.findAll();
        assertThat(qualificationsList).hasSize(databaseSizeBeforeUpdate);
        Qualifications testQualifications = qualificationsList.get(qualificationsList.size() - 1);
        assertThat(testQualifications.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testQualifications.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testQualifications.getExpireDate()).isEqualTo(UPDATED_EXPIRE_DATE);
        assertThat(testQualifications.getComments()).isEqualTo(UPDATED_COMMENTS);
        assertThat(testQualifications.isSubsidized()).isEqualTo(UPDATED_SUBSIDIZED);
    }

    @Test
    @Transactional
    public void updateNonExistingQualifications() throws Exception {
        int databaseSizeBeforeUpdate = qualificationsRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQualificationsMockMvc.perform(put("/api/qualifications")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(qualifications)))
            .andExpect(status().isBadRequest());

        // Validate the Qualifications in the database
        List<Qualifications> qualificationsList = qualificationsRepository.findAll();
        assertThat(qualificationsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteQualifications() throws Exception {
        // Initialize the database
        qualificationsService.save(qualifications);

        int databaseSizeBeforeDelete = qualificationsRepository.findAll().size();

        // Delete the qualifications
        restQualificationsMockMvc.perform(delete("/api/qualifications/{id}", qualifications.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Qualifications> qualificationsList = qualificationsRepository.findAll();
        assertThat(qualificationsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
