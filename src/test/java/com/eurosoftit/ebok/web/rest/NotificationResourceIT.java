package com.eurosoftit.ebok.web.rest;

import com.eurosoftit.ebok.EbokApp;
import com.eurosoftit.ebok.domain.Notification;
import com.eurosoftit.ebok.domain.User;
import com.eurosoftit.ebok.domain.enumeration.NotificationCategory;
import com.eurosoftit.ebok.repository.NotificationRepository;
import com.eurosoftit.ebok.service.NotificationService;
import com.eurosoftit.ebok.service.NotificationQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static com.eurosoftit.ebok.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link NotificationResource} REST controller.
 */
@SpringBootTest(classes = EbokApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class NotificationResourceIT {

    private static final String DEFAULT_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATE_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATE_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATE_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_READ_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_READ_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_READ_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final NotificationCategory DEFAULT_CATEGORY = NotificationCategory.MESSAGE;
    private static final NotificationCategory UPDATED_CATEGORY = NotificationCategory.SYSTEM;

    @Autowired
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationRepository notificationRepositoryMock;

    @Mock
    private NotificationService notificationServiceMock;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationQueryService notificationQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNotificationMockMvc;

    private Notification notification;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notification createEntity(EntityManager em) {
        Notification notification = new Notification()
            .subject(DEFAULT_SUBJECT)
            .description(DEFAULT_DESCRIPTION)
            .createDate(DEFAULT_CREATE_DATE)
            .readDate(DEFAULT_READ_DATE)
            .status(DEFAULT_STATUS)
            .category(DEFAULT_CATEGORY);
        return notification;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notification createUpdatedEntity(EntityManager em) {
        Notification notification = new Notification()
            .subject(UPDATED_SUBJECT)
            .description(UPDATED_DESCRIPTION)
            .createDate(UPDATED_CREATE_DATE)
            .readDate(UPDATED_READ_DATE)
            .status(UPDATED_STATUS)
            .category(UPDATED_CATEGORY);
        return notification;
    }

    @BeforeEach
    public void initTest() {
        notification = createEntity(em);
    }

    @Test
    @Transactional
    public void createNotification() throws Exception {
        int databaseSizeBeforeCreate = notificationRepository.findAll().size();
        // Create the Notification
        restNotificationMockMvc.perform(post("/api/notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(notification)))
            .andExpect(status().isCreated());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeCreate + 1);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testNotification.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testNotification.getCreateDate()).isEqualTo(DEFAULT_CREATE_DATE);
        assertThat(testNotification.getReadDate()).isEqualTo(DEFAULT_READ_DATE);
        assertThat(testNotification.isStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testNotification.getCategory()).isEqualTo(DEFAULT_CATEGORY);
    }

    @Test
    @Transactional
    public void createNotificationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = notificationRepository.findAll().size();

        // Create the Notification with an existing ID
        notification.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restNotificationMockMvc.perform(post("/api/notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(notification)))
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllNotifications() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList
        restNotificationMockMvc.perform(get("/api/notifications?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notification.getId().intValue())))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(sameInstant(DEFAULT_CREATE_DATE))))
            .andExpect(jsonPath("$.[*].readDate").value(hasItem(sameInstant(DEFAULT_READ_DATE))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.booleanValue())))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())));
    }

    @SuppressWarnings({"unchecked"})
    public void getAllNotificationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(notificationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restNotificationMockMvc.perform(get("/api/notifications?eagerload=true"))
            .andExpect(status().isOk());

        verify(notificationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllNotificationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(notificationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restNotificationMockMvc.perform(get("/api/notifications?eagerload=true"))
            .andExpect(status().isOk());

        verify(notificationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getNotification() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get the notification
        restNotificationMockMvc.perform(get("/api/notifications/{id}", notification.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notification.getId().intValue()))
            .andExpect(jsonPath("$.subject").value(DEFAULT_SUBJECT))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.createDate").value(sameInstant(DEFAULT_CREATE_DATE)))
            .andExpect(jsonPath("$.readDate").value(sameInstant(DEFAULT_READ_DATE)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.booleanValue()))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.toString()));
    }


    @Test
    @Transactional
    public void getNotificationsByIdFiltering() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        Long id = notification.getId();

        defaultNotificationShouldBeFound("id.equals=" + id);
        defaultNotificationShouldNotBeFound("id.notEquals=" + id);

        defaultNotificationShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultNotificationShouldNotBeFound("id.greaterThan=" + id);

        defaultNotificationShouldBeFound("id.lessThanOrEqual=" + id);
        defaultNotificationShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllNotificationsBySubjectIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where subject equals to DEFAULT_SUBJECT
        defaultNotificationShouldBeFound("subject.equals=" + DEFAULT_SUBJECT);

        // Get all the notificationList where subject equals to UPDATED_SUBJECT
        defaultNotificationShouldNotBeFound("subject.equals=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    public void getAllNotificationsBySubjectIsNotEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where subject not equals to DEFAULT_SUBJECT
        defaultNotificationShouldNotBeFound("subject.notEquals=" + DEFAULT_SUBJECT);

        // Get all the notificationList where subject not equals to UPDATED_SUBJECT
        defaultNotificationShouldBeFound("subject.notEquals=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    public void getAllNotificationsBySubjectIsInShouldWork() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where subject in DEFAULT_SUBJECT or UPDATED_SUBJECT
        defaultNotificationShouldBeFound("subject.in=" + DEFAULT_SUBJECT + "," + UPDATED_SUBJECT);

        // Get all the notificationList where subject equals to UPDATED_SUBJECT
        defaultNotificationShouldNotBeFound("subject.in=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    public void getAllNotificationsBySubjectIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where subject is not null
        defaultNotificationShouldBeFound("subject.specified=true");

        // Get all the notificationList where subject is null
        defaultNotificationShouldNotBeFound("subject.specified=false");
    }
                @Test
    @Transactional
    public void getAllNotificationsBySubjectContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where subject contains DEFAULT_SUBJECT
        defaultNotificationShouldBeFound("subject.contains=" + DEFAULT_SUBJECT);

        // Get all the notificationList where subject contains UPDATED_SUBJECT
        defaultNotificationShouldNotBeFound("subject.contains=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    public void getAllNotificationsBySubjectNotContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where subject does not contain DEFAULT_SUBJECT
        defaultNotificationShouldNotBeFound("subject.doesNotContain=" + DEFAULT_SUBJECT);

        // Get all the notificationList where subject does not contain UPDATED_SUBJECT
        defaultNotificationShouldBeFound("subject.doesNotContain=" + UPDATED_SUBJECT);
    }


    @Test
    @Transactional
    public void getAllNotificationsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where description equals to DEFAULT_DESCRIPTION
        defaultNotificationShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the notificationList where description equals to UPDATED_DESCRIPTION
        defaultNotificationShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllNotificationsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where description not equals to DEFAULT_DESCRIPTION
        defaultNotificationShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the notificationList where description not equals to UPDATED_DESCRIPTION
        defaultNotificationShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllNotificationsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultNotificationShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the notificationList where description equals to UPDATED_DESCRIPTION
        defaultNotificationShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllNotificationsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where description is not null
        defaultNotificationShouldBeFound("description.specified=true");

        // Get all the notificationList where description is null
        defaultNotificationShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllNotificationsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where description contains DEFAULT_DESCRIPTION
        defaultNotificationShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the notificationList where description contains UPDATED_DESCRIPTION
        defaultNotificationShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllNotificationsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where description does not contain DEFAULT_DESCRIPTION
        defaultNotificationShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the notificationList where description does not contain UPDATED_DESCRIPTION
        defaultNotificationShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllNotificationsByCreateDateIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where createDate equals to DEFAULT_CREATE_DATE
        defaultNotificationShouldBeFound("createDate.equals=" + DEFAULT_CREATE_DATE);

        // Get all the notificationList where createDate equals to UPDATED_CREATE_DATE
        defaultNotificationShouldNotBeFound("createDate.equals=" + UPDATED_CREATE_DATE);
    }

    @Test
    @Transactional
    public void getAllNotificationsByCreateDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where createDate not equals to DEFAULT_CREATE_DATE
        defaultNotificationShouldNotBeFound("createDate.notEquals=" + DEFAULT_CREATE_DATE);

        // Get all the notificationList where createDate not equals to UPDATED_CREATE_DATE
        defaultNotificationShouldBeFound("createDate.notEquals=" + UPDATED_CREATE_DATE);
    }

    @Test
    @Transactional
    public void getAllNotificationsByCreateDateIsInShouldWork() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where createDate in DEFAULT_CREATE_DATE or UPDATED_CREATE_DATE
        defaultNotificationShouldBeFound("createDate.in=" + DEFAULT_CREATE_DATE + "," + UPDATED_CREATE_DATE);

        // Get all the notificationList where createDate equals to UPDATED_CREATE_DATE
        defaultNotificationShouldNotBeFound("createDate.in=" + UPDATED_CREATE_DATE);
    }

    @Test
    @Transactional
    public void getAllNotificationsByCreateDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where createDate is not null
        defaultNotificationShouldBeFound("createDate.specified=true");

        // Get all the notificationList where createDate is null
        defaultNotificationShouldNotBeFound("createDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllNotificationsByCreateDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where createDate is greater than or equal to DEFAULT_CREATE_DATE
        defaultNotificationShouldBeFound("createDate.greaterThanOrEqual=" + DEFAULT_CREATE_DATE);

        // Get all the notificationList where createDate is greater than or equal to UPDATED_CREATE_DATE
        defaultNotificationShouldNotBeFound("createDate.greaterThanOrEqual=" + UPDATED_CREATE_DATE);
    }

    @Test
    @Transactional
    public void getAllNotificationsByCreateDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where createDate is less than or equal to DEFAULT_CREATE_DATE
        defaultNotificationShouldBeFound("createDate.lessThanOrEqual=" + DEFAULT_CREATE_DATE);

        // Get all the notificationList where createDate is less than or equal to SMALLER_CREATE_DATE
        defaultNotificationShouldNotBeFound("createDate.lessThanOrEqual=" + SMALLER_CREATE_DATE);
    }

    @Test
    @Transactional
    public void getAllNotificationsByCreateDateIsLessThanSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where createDate is less than DEFAULT_CREATE_DATE
        defaultNotificationShouldNotBeFound("createDate.lessThan=" + DEFAULT_CREATE_DATE);

        // Get all the notificationList where createDate is less than UPDATED_CREATE_DATE
        defaultNotificationShouldBeFound("createDate.lessThan=" + UPDATED_CREATE_DATE);
    }

    @Test
    @Transactional
    public void getAllNotificationsByCreateDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where createDate is greater than DEFAULT_CREATE_DATE
        defaultNotificationShouldNotBeFound("createDate.greaterThan=" + DEFAULT_CREATE_DATE);

        // Get all the notificationList where createDate is greater than SMALLER_CREATE_DATE
        defaultNotificationShouldBeFound("createDate.greaterThan=" + SMALLER_CREATE_DATE);
    }


    @Test
    @Transactional
    public void getAllNotificationsByReadDateIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where readDate equals to DEFAULT_READ_DATE
        defaultNotificationShouldBeFound("readDate.equals=" + DEFAULT_READ_DATE);

        // Get all the notificationList where readDate equals to UPDATED_READ_DATE
        defaultNotificationShouldNotBeFound("readDate.equals=" + UPDATED_READ_DATE);
    }

    @Test
    @Transactional
    public void getAllNotificationsByReadDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where readDate not equals to DEFAULT_READ_DATE
        defaultNotificationShouldNotBeFound("readDate.notEquals=" + DEFAULT_READ_DATE);

        // Get all the notificationList where readDate not equals to UPDATED_READ_DATE
        defaultNotificationShouldBeFound("readDate.notEquals=" + UPDATED_READ_DATE);
    }

    @Test
    @Transactional
    public void getAllNotificationsByReadDateIsInShouldWork() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where readDate in DEFAULT_READ_DATE or UPDATED_READ_DATE
        defaultNotificationShouldBeFound("readDate.in=" + DEFAULT_READ_DATE + "," + UPDATED_READ_DATE);

        // Get all the notificationList where readDate equals to UPDATED_READ_DATE
        defaultNotificationShouldNotBeFound("readDate.in=" + UPDATED_READ_DATE);
    }

    @Test
    @Transactional
    public void getAllNotificationsByReadDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where readDate is not null
        defaultNotificationShouldBeFound("readDate.specified=true");

        // Get all the notificationList where readDate is null
        defaultNotificationShouldNotBeFound("readDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllNotificationsByReadDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where readDate is greater than or equal to DEFAULT_READ_DATE
        defaultNotificationShouldBeFound("readDate.greaterThanOrEqual=" + DEFAULT_READ_DATE);

        // Get all the notificationList where readDate is greater than or equal to UPDATED_READ_DATE
        defaultNotificationShouldNotBeFound("readDate.greaterThanOrEqual=" + UPDATED_READ_DATE);
    }

    @Test
    @Transactional
    public void getAllNotificationsByReadDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where readDate is less than or equal to DEFAULT_READ_DATE
        defaultNotificationShouldBeFound("readDate.lessThanOrEqual=" + DEFAULT_READ_DATE);

        // Get all the notificationList where readDate is less than or equal to SMALLER_READ_DATE
        defaultNotificationShouldNotBeFound("readDate.lessThanOrEqual=" + SMALLER_READ_DATE);
    }

    @Test
    @Transactional
    public void getAllNotificationsByReadDateIsLessThanSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where readDate is less than DEFAULT_READ_DATE
        defaultNotificationShouldNotBeFound("readDate.lessThan=" + DEFAULT_READ_DATE);

        // Get all the notificationList where readDate is less than UPDATED_READ_DATE
        defaultNotificationShouldBeFound("readDate.lessThan=" + UPDATED_READ_DATE);
    }

    @Test
    @Transactional
    public void getAllNotificationsByReadDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where readDate is greater than DEFAULT_READ_DATE
        defaultNotificationShouldNotBeFound("readDate.greaterThan=" + DEFAULT_READ_DATE);

        // Get all the notificationList where readDate is greater than SMALLER_READ_DATE
        defaultNotificationShouldBeFound("readDate.greaterThan=" + SMALLER_READ_DATE);
    }


    @Test
    @Transactional
    public void getAllNotificationsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where status equals to DEFAULT_STATUS
        defaultNotificationShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the notificationList where status equals to UPDATED_STATUS
        defaultNotificationShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllNotificationsByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where status not equals to DEFAULT_STATUS
        defaultNotificationShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the notificationList where status not equals to UPDATED_STATUS
        defaultNotificationShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllNotificationsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultNotificationShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the notificationList where status equals to UPDATED_STATUS
        defaultNotificationShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllNotificationsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where status is not null
        defaultNotificationShouldBeFound("status.specified=true");

        // Get all the notificationList where status is null
        defaultNotificationShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    public void getAllNotificationsByCategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where category equals to DEFAULT_CATEGORY
        defaultNotificationShouldBeFound("category.equals=" + DEFAULT_CATEGORY);

        // Get all the notificationList where category equals to UPDATED_CATEGORY
        defaultNotificationShouldNotBeFound("category.equals=" + UPDATED_CATEGORY);
    }

    @Test
    @Transactional
    public void getAllNotificationsByCategoryIsNotEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where category not equals to DEFAULT_CATEGORY
        defaultNotificationShouldNotBeFound("category.notEquals=" + DEFAULT_CATEGORY);

        // Get all the notificationList where category not equals to UPDATED_CATEGORY
        defaultNotificationShouldBeFound("category.notEquals=" + UPDATED_CATEGORY);
    }

    @Test
    @Transactional
    public void getAllNotificationsByCategoryIsInShouldWork() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where category in DEFAULT_CATEGORY or UPDATED_CATEGORY
        defaultNotificationShouldBeFound("category.in=" + DEFAULT_CATEGORY + "," + UPDATED_CATEGORY);

        // Get all the notificationList where category equals to UPDATED_CATEGORY
        defaultNotificationShouldNotBeFound("category.in=" + UPDATED_CATEGORY);
    }

    @Test
    @Transactional
    public void getAllNotificationsByCategoryIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where category is not null
        defaultNotificationShouldBeFound("category.specified=true");

        // Get all the notificationList where category is null
        defaultNotificationShouldNotBeFound("category.specified=false");
    }

    @Test
    @Transactional
    public void getAllNotificationsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);
        User createdBy = UserResourceIT.createEntity(em);
        em.persist(createdBy);
        em.flush();
        notification.setCreatedBy(createdBy);
        notificationRepository.saveAndFlush(notification);
        Long createdById = createdBy.getId();

        // Get all the notificationList where createdBy equals to createdById
        defaultNotificationShouldBeFound("createdById.equals=" + createdById);

        // Get all the notificationList where createdBy equals to createdById + 1
        defaultNotificationShouldNotBeFound("createdById.equals=" + (createdById + 1));
    }


    @Test
    @Transactional
    public void getAllNotificationsByRecepientIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);
        User recepient = UserResourceIT.createEntity(em);
        em.persist(recepient);
        em.flush();
        notification.addRecepient(recepient);
        notificationRepository.saveAndFlush(notification);
        Long recepientId = recepient.getId();

        // Get all the notificationList where recepient equals to recepientId
        defaultNotificationShouldBeFound("recepientId.equals=" + recepientId);

        // Get all the notificationList where recepient equals to recepientId + 1
        defaultNotificationShouldNotBeFound("recepientId.equals=" + (recepientId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultNotificationShouldBeFound(String filter) throws Exception {
        restNotificationMockMvc.perform(get("/api/notifications?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notification.getId().intValue())))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(sameInstant(DEFAULT_CREATE_DATE))))
            .andExpect(jsonPath("$.[*].readDate").value(hasItem(sameInstant(DEFAULT_READ_DATE))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.booleanValue())))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())));

        // Check, that the count call also returns 1
        restNotificationMockMvc.perform(get("/api/notifications/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultNotificationShouldNotBeFound(String filter) throws Exception {
        restNotificationMockMvc.perform(get("/api/notifications?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restNotificationMockMvc.perform(get("/api/notifications/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingNotification() throws Exception {
        // Get the notification
        restNotificationMockMvc.perform(get("/api/notifications/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateNotification() throws Exception {
        // Initialize the database
        notificationService.save(notification);

        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();

        // Update the notification
        Notification updatedNotification = notificationRepository.findById(notification.getId()).get();
        // Disconnect from session so that the updates on updatedNotification are not directly saved in db
        em.detach(updatedNotification);
        updatedNotification
            .subject(UPDATED_SUBJECT)
            .description(UPDATED_DESCRIPTION)
            .createDate(UPDATED_CREATE_DATE)
            .readDate(UPDATED_READ_DATE)
            .status(UPDATED_STATUS)
            .category(UPDATED_CATEGORY);

        restNotificationMockMvc.perform(put("/api/notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedNotification)))
            .andExpect(status().isOk());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testNotification.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testNotification.getCreateDate()).isEqualTo(UPDATED_CREATE_DATE);
        assertThat(testNotification.getReadDate()).isEqualTo(UPDATED_READ_DATE);
        assertThat(testNotification.isStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testNotification.getCategory()).isEqualTo(UPDATED_CATEGORY);
    }

    @Test
    @Transactional
    public void updateNonExistingNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationMockMvc.perform(put("/api/notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(notification)))
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteNotification() throws Exception {
        // Initialize the database
        notificationService.save(notification);

        int databaseSizeBeforeDelete = notificationRepository.findAll().size();

        // Delete the notification
        restNotificationMockMvc.perform(delete("/api/notifications/{id}", notification.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
