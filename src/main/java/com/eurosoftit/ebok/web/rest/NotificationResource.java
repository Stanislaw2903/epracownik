package com.eurosoftit.ebok.web.rest;

import com.eurosoftit.ebok.domain.Notification;
import com.eurosoftit.ebok.domain.NotificationDTO;
import com.eurosoftit.ebok.domain.NotificationRead;
import com.eurosoftit.ebok.domain.User;
import com.eurosoftit.ebok.domain.enumeration.NotificationCanal;
import com.eurosoftit.ebok.domain.specification.SpecificationReflectionBuilder;
import com.eurosoftit.ebok.repository.NotificationReadRepository;
import com.eurosoftit.ebok.repository.UserRepository;
import com.eurosoftit.ebok.security.SecurityUtils;
import com.eurosoftit.ebok.service.NotificationMapper;
import com.eurosoftit.ebok.service.NotificationQueryService;
import com.eurosoftit.ebok.service.NotificationService;
import com.eurosoftit.ebok.service.UserNotificationSender;
import com.eurosoftit.ebok.service.dto.NotificationCriteria;
import com.eurosoftit.ebok.web.rest.errors.BadRequestAlertException;
import com.eurosoftit.ebok.web.rest.errors.NotificationResourceException;
import com.eurosoftit.ebok.web.rest.group.UserGroupEntity;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import liquibase.util.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for managing {@link com.eurosoftit.ebok.domain.Notification}.
 */
@RestController
@RequestMapping("/api")
public class NotificationResource {

    private final Logger log = LoggerFactory.getLogger(NotificationResource.class);

    private static final String ENTITY_NAME = "notification";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Value("${smsapi.url:''}")
    private String smsapiUrl;

    private UserRepository userRepository;
    private NotificationReadRepository notificationReadRepository;

    private final NotificationService notificationService;
    private final NotificationQueryService notificationQueryService;
    private final UserUtils userUtils;
    private final NotificationMapper notificationMapper;
    private final UserNotificationSender messageResource;

    @PersistenceContext
    private EntityManager entityManager;

    public NotificationResource(
        NotificationService notificationService,
        NotificationQueryService notificationQueryService,
        UserRepository userRepository,
        NotificationReadRepository notificationReadRepository,
        UserUtils userUtils,
        NotificationMapper notificationMapper,
        UserNotificationSender messageResource) {

        this.notificationService = notificationService;
        this.notificationQueryService = notificationQueryService;
        this.userRepository = userRepository;
        this.notificationReadRepository = notificationReadRepository;
        this.userUtils = userUtils;
        this.notificationMapper = notificationMapper;
        this.messageResource = messageResource;
    }

    /**
     * {@code POST  /notifications} : Create a new notification.
     *
     * @param notification the notification to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new notification, or with status {@code 400 (Bad Request)} if the notification has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/notifications")
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) throws URISyntaxException {
        log.debug("REST request to save Notification : {}", notification);
        if (notification.getId() != null) {
            throw new BadRequestAlertException("A new notification cannot already have an ID", ENTITY_NAME, "idexists");
        }
        String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new NotificationResourceException("Current user login not found"));

        Optional<User> user = userRepository.findOneByLogin(userLogin);
        if (!user.isPresent()) {
            throw new NotificationResourceException("User could not be found");
        }
        notification.setCreatedBy(user.get());
        final Notification result = notificationService.save(notification);

        if (result.getSendSms() != null && result.getSendSms() && messageResource.getNotificationType() == UserNotificationSender.NotificationType.SMS) {
            Set<User> users = getUsers(result);
            List<String> phoneNumbersByIds = users.stream()
                .filter(User::getActivated).map(User::getPhoneNumber)
                .filter(StringUtils::isNotBlank)
                .filter(Objects::nonNull).collect(Collectors.toList());

            if (phoneNumbersByIds != null && !phoneNumbersByIds.isEmpty()) {
                final String content = "e_Pracownik: " + result.getSubject() + ": " + result.getDescription();
                this.messageResource.sendSms(phoneNumbersByIds, content);
            }
        } else if (result.getSendMail() != null && result.getSendMail() && (messageResource.getNotificationType() == UserNotificationSender.NotificationType.MAIL || messageResource.getNotificationType() == UserNotificationSender.NotificationType.LOCAL_MAIL)) {
            Set<User> users = getUsers(result);

            if (!CollectionUtils.isEmpty(users)) {
                List<String> emails = users.stream()
                    .filter(User::getActivated).map(User::getEmail)
                    .filter(StringUtils::isNotBlank)
                    .filter(Objects::nonNull).collect(Collectors.toList());

                if (emails != null && !emails.isEmpty()) {
                    this.messageResource.sendEmail(emails, result.getDescription(), StringUtils.isBlank(result.getSubject()) ? "e_Pracownik - powiadomienie" : result.getSubject());
                }
            }
        }

        return ResponseEntity.created(new

            URI("/api/notifications/" + result.getId()))
            .

                headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().

                    toString()))
            .

                body(result);

    }

    private Set<User> getUsers(Notification result) {
        Set<User> users = null;
        if (result.getCanal() == null) {
            if (!CollectionUtils.isEmpty(result.getRecepients())) {
                users = result.getRecepients();
            }

            Set<UserGroupEntity> groups = result.getGroups();
            if (!CollectionUtils.isEmpty(groups)) {
                users = userRepository.findByGroupIds(groups.stream().map(group -> group.getId()).collect(Collectors.toList()));
            }
        } else {
            switch (result.getCanal()) {
                case ALL:
                    UserProperties userProperties = new UserProperties();
                    userProperties.setActivated(true);
                    final Specification<User> process = SpecificationReflectionBuilder.process(userProperties);
                    users = userRepository.findAll(process).stream().collect(Collectors.toSet());
                    break;
                case GROUPS:
                    Set<UserGroupEntity> groups = result.getGroups();
                    if (!CollectionUtils.isEmpty(groups)) {
                        users = userRepository.findByGroupIds(groups.stream().map(group -> group.getId()).collect(Collectors.toList()));
                    }
                    break;
                case USERS:
                    if (!CollectionUtils.isEmpty(result.getRecepients())) {
                        users = result.getRecepients();
                    }
                    break;
            }

        }
        return users;
    }

    /**
     * {@code PUT  /notifications} : Updates an existing notification.
     *
     * @param notification the notification to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notification,
     * or with status {@code 400 (Bad Request)} if the notification is not valid,
     * or with status {@code 500 (Internal Server Error)} if the notification couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/notifications")
    public ResponseEntity<Notification> updateNotification(@RequestBody Notification notification) throws URISyntaxException {
        log.debug("REST request to update Notification : {}", notification);
        if (notification.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Notification result = notificationService.save(notification);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notification.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /notifications} : get all the notifications.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notifications in body.
     */
    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDTO>> getAllNotifications(NotificationCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Notifications by criteria: {}", criteria);
        Page<NotificationDTO> page = notificationMapper.toDTO(notificationQueryService.findByCriteria(criteria, pageable));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /user-notifications} : get current user notifications.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notifications in body.
     */
    @GetMapping("/notifications/user")
    public ResponseEntity<List<Notification>> getUserNotifications(NotificationCriteria criteria, Pageable pageable) {

        NotificationCriteria.notificationCanalFilter canalFilter = new NotificationCriteria.notificationCanalFilter();
        canalFilter.setIn(Arrays.asList(NotificationCanal.ALL));
        criteria.setNotCreatedById(getUserId());
        criteria.setRecepientId(getUserId());
        criteria.setGroupId(getGroupUserId());
        criteria.setCanal(canalFilter);
        criteria.onlySendNotification();
        log.debug("REST request to get UserNotifications by criteria: {}", criteria);

        Page<Notification> page = notificationQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /notifications/user/:id} : get the "id" notification.
     *
     * @param id the id of the notification to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notification, or with status {@code 404 (Not Found)}.
     */
    @Transactional
    @GetMapping("/notifications/user/{id}")
    public ResponseEntity<Notification> getUserNotification(@PathVariable Long id) {
        NotificationCriteria criteria = new NotificationCriteria();

        NotificationCriteria.notificationCanalFilter canalFilter = new NotificationCriteria.notificationCanalFilter();
        canalFilter.setIn(Arrays.asList(NotificationCanal.ALL));
        criteria.setGroupId(getGroupUserId());
        criteria.setCanal(canalFilter);
        criteria.setNotCreatedById(getUserId());
        criteria.setRecepientId(getUserId());
        criteria.setId(convertToLongFilter(id));
        log.debug("REST request to get user Notification : {}", criteria);
        Optional<Notification> notification = notificationQueryService.findOneByCriteria(criteria);
//        Set notification as read
        if (notification.isPresent()) {
            Notification existingNotification = notification.get();
            NotificationRead notificationRead = new NotificationRead();
            notificationRead.setDate(ZonedDateTime.now());
            notificationRead.setNotify(existingNotification.getId().intValue());

            notificationRead.addUser(getUser());
            notificationReadRepository.save(notificationRead);

            if (!BooleanUtils.isTrue(existingNotification.isStatus())) {
                existingNotification.setStatus(true);
                existingNotification.setReadDate(ZonedDateTime.now());
                notificationService.save(existingNotification);
            }
        }
        return ResponseUtil.wrapOrNotFound(notification);
    }

    /**
     * {@code GET  /notifications/count} : count all the notifications.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/notifications/count")
    public ResponseEntity<Long> countNotifications(NotificationCriteria criteria) {
        log.debug("REST request to count Notifications by criteria: {}", criteria);
        return ResponseEntity.ok().body(notificationQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /notifications/:id} : get the "id" notification.
     *
     * @param id the id of the notification to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notification, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/notifications/{id}")
    public ResponseEntity<Notification> getNotification(@PathVariable Long id) {
        log.debug("REST request to get Notification : {}", id);
        Optional<Notification> notification = notificationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(notification);
    }

    /**
     * {@code DELETE  /notifications/:id} : delete the "id" notification.
     *
     * @param id the id of the notification to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/notifications/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        log.debug("REST request to delete Notification : {}", id);
        notificationService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    private LongFilter convertToLongFilter(Long value) {
        if (value == null) {
            return null;
        }
        Long longValue = value;
        LongFilter longFilter = new LongFilter();
        longFilter.setEquals(longValue);
        return longFilter;
    }

    private User getUser() {
        return entityManager.getReference(User.class, userUtils.getLoggedUserId());
    }

    private LongFilter getUserId() {
        return convertToLongFilter(userUtils.getLoggedUserId());
    }

    private LongFilter getGroupUserId() {
        return convertToLongFilter(userUtils.getLoggedUserGroupId());
    }
}
