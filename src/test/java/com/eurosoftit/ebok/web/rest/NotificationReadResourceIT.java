package com.eurosoftit.ebok.web.rest;

import com.eurosoftit.ebok.EbokApp;
import com.eurosoftit.ebok.domain.NotificationRead;
import com.eurosoftit.ebok.domain.User;
import com.eurosoftit.ebok.repository.NotificationReadRepository;
import com.eurosoftit.ebok.service.NotificationReadService;
import com.eurosoftit.ebok.service.dto.NotificationReadCriteria;
import com.eurosoftit.ebok.service.NotificationReadQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
 * Integration tests for the {@link NotificationReadResource} REST controller.
 */
@SpringBootTest(classes = EbokApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class NotificationReadResourceIT {

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final Integer DEFAULT_NOTIFY = 1;
    private static final Integer UPDATED_NOTIFY = 2;
    private static final Integer SMALLER_NOTIFY = 1 - 1;

    @Autowired
    private NotificationReadRepository notificationReadRepository;

    @Mock
    private NotificationReadRepository notificationReadRepositoryMock;

    @Mock
    private NotificationReadService notificationReadServiceMock;

    @Autowired
    private NotificationReadService notificationReadService;

    @Autowired
    private NotificationReadQueryService notificationReadQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNotificationReadMockMvc;

    private NotificationRead notificationRead;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotificationRead createEntity(EntityManager em) {
        NotificationRead notificationRead = new NotificationRead()
            .date(DEFAULT_DATE)
            .notify(DEFAULT_NOTIFY);
        return notificationRead;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotificationRead createUpdatedEntity(EntityManager em) {
        NotificationRead notificationRead = new NotificationRead()
            .date(UPDATED_DATE)
            .notify(UPDATED_NOTIFY);
        return notificationRead;
    }

    @BeforeEach
    public void initTest() {
        notificationRead = createEntity(em);
    }

    @Test
    @Transactional
    public void createNotificationRead() throws Exception {
        int databaseSizeBeforeCreate = notificationReadRepository.findAll().size();
        // Create the NotificationRead
        restNotificationReadMockMvc.perform(post("/api/notification-reads")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(notificationRead)))
            .andExpect(status().isCreated());

        // Validate the NotificationRead in the database
        List<NotificationRead> notificationReadList = notificationReadRepository.findAll();
        assertThat(notificationReadList).hasSize(databaseSizeBeforeCreate + 1);
        NotificationRead testNotificationRead = notificationReadList.get(notificationReadList.size() - 1);
        assertThat(testNotificationRead.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testNotificationRead.getNotify()).isEqualTo(DEFAULT_NOTIFY);
    }

    @Test
    @Transactional
    public void createNotificationReadWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = notificationReadRepository.findAll().size();

        // Create the NotificationRead with an existing ID
        notificationRead.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restNotificationReadMockMvc.perform(post("/api/notification-reads")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(notificationRead)))
            .andExpect(status().isBadRequest());

        // Validate the NotificationRead in the database
        List<NotificationRead> notificationReadList = notificationReadRepository.findAll();
        assertThat(notificationReadList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllNotificationReads() throws Exception {
        // Initialize the database
        notificationReadRepository.saveAndFlush(notificationRead);

        // Get all the notificationReadList
        restNotificationReadMockMvc.perform(get("/api/notification-reads?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notificationRead.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].notify").value(hasItem(DEFAULT_NOTIFY)));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllNotificationReadsWithEagerRelationshipsIsEnabled() throws Exception {
        when(notificationReadServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restNotificationReadMockMvc.perform(get("/api/notification-reads?eagerload=true"))
            .andExpect(status().isOk());

        verify(notificationReadServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllNotificationReadsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(notificationReadServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restNotificationReadMockMvc.perform(get("/api/notification-reads?eagerload=true"))
            .andExpect(status().isOk());

        verify(notificationReadServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getNotificationRead() throws Exception {
        // Initialize the database
        notificationReadRepository.saveAndFlush(notificationRead);

        // Get the notificationRead
        restNotificationReadMockMvc.perform(get("/api/notification-reads/{id}", notificationRead.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notificationRead.getId().intValue()))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)))
            .andExpect(jsonPath("$.notify").value(DEFAULT_NOTIFY));
    }


    @Test
    @Transactional
    public void getNotificationReadsByIdFiltering() throws Exception {
        // Initialize the database
        notificationReadRepository.saveAndFlush(notificationRead);

        Long id = notificationRead.getId();

        defaultNotificationReadShouldBeFound("id.equals=" + id);
        defaultNotificationReadShouldNotBeFound("id.notEquals=" + id);

        defaultNotificationReadShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultNotificationReadShouldNotBeFound("id.greaterThan=" + id);

        defaultNotificationReadShouldBeFound("id.lessThanOrEqual=" + id);
        defaultNotificationReadShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllNotificationReadsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationReadRepository.saveAndFlush(notificationRead);

        // Get all the notificationReadList where date equals to DEFAULT_DATE
        defaultNotificationReadShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the notificationReadList where date equals to UPDATED_DATE
        defaultNotificationReadShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllNotificationReadsByDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        notificationReadRepository.saveAndFlush(notificationRead);

        // Get all the notificationReadList where date not equals to DEFAULT_DATE
        defaultNotificationReadShouldNotBeFound("date.notEquals=" + DEFAULT_DATE);

        // Get all the notificationReadList where date not equals to UPDATED_DATE
        defaultNotificationReadShouldBeFound("date.notEquals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllNotificationReadsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        notificationReadRepository.saveAndFlush(notificationRead);

        // Get all the notificationReadList where date in DEFAULT_DATE or UPDATED_DATE
        defaultNotificationReadShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the notificationReadList where date equals to UPDATED_DATE
        defaultNotificationReadShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllNotificationReadsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationReadRepository.saveAndFlush(notificationRead);

        // Get all the notificationReadList where date is not null
        defaultNotificationReadShouldBeFound("date.specified=true");

        // Get all the notificationReadList where date is null
        defaultNotificationReadShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    public void getAllNotificationReadsByDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        notificationReadRepository.saveAndFlush(notificationRead);

        // Get all the notificationReadList where date is greater than or equal to DEFAULT_DATE
        defaultNotificationReadShouldBeFound("date.greaterThanOrEqual=" + DEFAULT_DATE);

        // Get all the notificationReadList where date is greater than or equal to UPDATED_DATE
        defaultNotificationReadShouldNotBeFound("date.greaterThanOrEqual=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllNotificationReadsByDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        notificationReadRepository.saveAndFlush(notificationRead);

        // Get all the notificationReadList where date is less than or equal to DEFAULT_DATE
        defaultNotificationReadShouldBeFound("date.lessThanOrEqual=" + DEFAULT_DATE);

        // Get all the notificationReadList where date is less than or equal to SMALLER_DATE
        defaultNotificationReadShouldNotBeFound("date.lessThanOrEqual=" + SMALLER_DATE);
    }

    @Test
    @Transactional
    public void getAllNotificationReadsByDateIsLessThanSomething() throws Exception {
        // Initialize the database
        notificationReadRepository.saveAndFlush(notificationRead);

        // Get all the notificationReadList where date is less than DEFAULT_DATE
        defaultNotificationReadShouldNotBeFound("date.lessThan=" + DEFAULT_DATE);

        // Get all the notificationReadList where date is less than UPDATED_DATE
        defaultNotificationReadShouldBeFound("date.lessThan=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllNotificationReadsByDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        notificationReadRepository.saveAndFlush(notificationRead);

        // Get all the notificationReadList where date is greater than DEFAULT_DATE
        defaultNotificationReadShouldNotBeFound("date.greaterThan=" + DEFAULT_DATE);

        // Get all the notificationReadList where date is greater than SMALLER_DATE
        defaultNotificationReadShouldBeFound("date.greaterThan=" + SMALLER_DATE);
    }


    @Test
    @Transactional
    public void getAllNotificationReadsByNotifyIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationReadRepository.saveAndFlush(notificationRead);

        // Get all the notificationReadList where notify equals to DEFAULT_NOTIFY
        defaultNotificationReadShouldBeFound("notify.equals=" + DEFAULT_NOTIFY);

        // Get all the notificationReadList where notify equals to UPDATED_NOTIFY
        defaultNotificationReadShouldNotBeFound("notify.equals=" + UPDATED_NOTIFY);
    }

    @Test
    @Transactional
    public void getAllNotificationReadsByNotifyIsNotEqualToSomething() throws Exception {
        // Initialize the database
        notificationReadRepository.saveAndFlush(notificationRead);

        // Get all the notificationReadList where notify not equals to DEFAULT_NOTIFY
        defaultNotificationReadShouldNotBeFound("notify.notEquals=" + DEFAULT_NOTIFY);

        // Get all the notificationReadList where notify not equals to UPDATED_NOTIFY
        defaultNotificationReadShouldBeFound("notify.notEquals=" + UPDATED_NOTIFY);
    }

    @Test
    @Transactional
    public void getAllNotificationReadsByNotifyIsInShouldWork() throws Exception {
        // Initialize the database
        notificationReadRepository.saveAndFlush(notificationRead);

        // Get all the notificationReadList where notify in DEFAULT_NOTIFY or UPDATED_NOTIFY
        defaultNotificationReadShouldBeFound("notify.in=" + DEFAULT_NOTIFY + "," + UPDATED_NOTIFY);

        // Get all the notificationReadList where notify equals to UPDATED_NOTIFY
        defaultNotificationReadShouldNotBeFound("notify.in=" + UPDATED_NOTIFY);
    }

    @Test
    @Transactional
    public void getAllNotificationReadsByNotifyIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationReadRepository.saveAndFlush(notificationRead);

        // Get all the notificationReadList where notify is not null
        defaultNotificationReadShouldBeFound("notify.specified=true");

        // Get all the notificationReadList where notify is null
        defaultNotificationReadShouldNotBeFound("notify.specified=false");
    }

    @Test
    @Transactional
    public void getAllNotificationReadsByNotifyIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        notificationReadRepository.saveAndFlush(notificationRead);

        // Get all the notificationReadList where notify is greater than or equal to DEFAULT_NOTIFY
        defaultNotificationReadShouldBeFound("notify.greaterThanOrEqual=" + DEFAULT_NOTIFY);

        // Get all the notificationReadList where notify is greater than or equal to UPDATED_NOTIFY
        defaultNotificationReadShouldNotBeFound("notify.greaterThanOrEqual=" + UPDATED_NOTIFY);
    }

    @Test
    @Transactional
    public void getAllNotificationReadsByNotifyIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        notificationReadRepository.saveAndFlush(notificationRead);

        // Get all the notificationReadList where notify is less than or equal to DEFAULT_NOTIFY
        defaultNotificationReadShouldBeFound("notify.lessThanOrEqual=" + DEFAULT_NOTIFY);

        // Get all the notificationReadList where notify is less than or equal to SMALLER_NOTIFY
        defaultNotificationReadShouldNotBeFound("notify.lessThanOrEqual=" + SMALLER_NOTIFY);
    }

    @Test
    @Transactional
    public void getAllNotificationReadsByNotifyIsLessThanSomething() throws Exception {
        // Initialize the database
        notificationReadRepository.saveAndFlush(notificationRead);

        // Get all the notificationReadList where notify is less than DEFAULT_NOTIFY
        defaultNotificationReadShouldNotBeFound("notify.lessThan=" + DEFAULT_NOTIFY);

        // Get all the notificationReadList where notify is less than UPDATED_NOTIFY
        defaultNotificationReadShouldBeFound("notify.lessThan=" + UPDATED_NOTIFY);
    }

    @Test
    @Transactional
    public void getAllNotificationReadsByNotifyIsGreaterThanSomething() throws Exception {
        // Initialize the database
        notificationReadRepository.saveAndFlush(notificationRead);

        // Get all the notificationReadList where notify is greater than DEFAULT_NOTIFY
        defaultNotificationReadShouldNotBeFound("notify.greaterThan=" + DEFAULT_NOTIFY);

        // Get all the notificationReadList where notify is greater than SMALLER_NOTIFY
        defaultNotificationReadShouldBeFound("notify.greaterThan=" + SMALLER_NOTIFY);
    }


    @Test
    @Transactional
    public void getAllNotificationReadsByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationReadRepository.saveAndFlush(notificationRead);
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        notificationRead.addUser(user);
        notificationReadRepository.saveAndFlush(notificationRead);
        Long userId = user.getId();

        // Get all the notificationReadList where user equals to userId
        defaultNotificationReadShouldBeFound("userId.equals=" + userId);

        // Get all the notificationReadList where user equals to userId + 1
        defaultNotificationReadShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultNotificationReadShouldBeFound(String filter) throws Exception {
        restNotificationReadMockMvc.perform(get("/api/notification-reads?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notificationRead.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].notify").value(hasItem(DEFAULT_NOTIFY)));

        // Check, that the count call also returns 1
        restNotificationReadMockMvc.perform(get("/api/notification-reads/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultNotificationReadShouldNotBeFound(String filter) throws Exception {
        restNotificationReadMockMvc.perform(get("/api/notification-reads?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restNotificationReadMockMvc.perform(get("/api/notification-reads/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingNotificationRead() throws Exception {
        // Get the notificationRead
        restNotificationReadMockMvc.perform(get("/api/notification-reads/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateNotificationRead() throws Exception {
        // Initialize the database
        notificationReadService.save(notificationRead);

        int databaseSizeBeforeUpdate = notificationReadRepository.findAll().size();

        // Update the notificationRead
        NotificationRead updatedNotificationRead = notificationReadRepository.findById(notificationRead.getId()).get();
        // Disconnect from session so that the updates on updatedNotificationRead are not directly saved in db
        em.detach(updatedNotificationRead);
        updatedNotificationRead
            .date(UPDATED_DATE)
            .notify(UPDATED_NOTIFY);

        restNotificationReadMockMvc.perform(put("/api/notification-reads")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedNotificationRead)))
            .andExpect(status().isOk());

        // Validate the NotificationRead in the database
        List<NotificationRead> notificationReadList = notificationReadRepository.findAll();
        assertThat(notificationReadList).hasSize(databaseSizeBeforeUpdate);
        NotificationRead testNotificationRead = notificationReadList.get(notificationReadList.size() - 1);
        assertThat(testNotificationRead.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testNotificationRead.getNotify()).isEqualTo(UPDATED_NOTIFY);
    }

    @Test
    @Transactional
    public void updateNonExistingNotificationRead() throws Exception {
        int databaseSizeBeforeUpdate = notificationReadRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationReadMockMvc.perform(put("/api/notification-reads")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(notificationRead)))
            .andExpect(status().isBadRequest());

        // Validate the NotificationRead in the database
        List<NotificationRead> notificationReadList = notificationReadRepository.findAll();
        assertThat(notificationReadList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteNotificationRead() throws Exception {
        // Initialize the database
        notificationReadService.save(notificationRead);

        int databaseSizeBeforeDelete = notificationReadRepository.findAll().size();

        // Delete the notificationRead
        restNotificationReadMockMvc.perform(delete("/api/notification-reads/{id}", notificationRead.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<NotificationRead> notificationReadList = notificationReadRepository.findAll();
        assertThat(notificationReadList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
