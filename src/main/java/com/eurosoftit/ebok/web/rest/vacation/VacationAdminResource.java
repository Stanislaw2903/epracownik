package com.eurosoftit.ebok.web.rest.vacation;

import com.eurosoftit.ebok.domain.DictionaryDTO;
import com.eurosoftit.ebok.domain.Notification;
import com.eurosoftit.ebok.domain.User;
import com.eurosoftit.ebok.repository.UserRepository;
import com.eurosoftit.ebok.security.AuthoritiesConstants;
import com.eurosoftit.ebok.service.NotificationService;
import com.eurosoftit.ebok.service.UserNotificationSender;
import com.eurosoftit.ebok.service.UserService;
import com.eurosoftit.ebok.service.dto.ApprovalType;
import com.eurosoftit.ebok.web.rest.UserUtils;
import com.eurosoftit.ebok.web.rest.errors.BadRequestAlertException;
import com.eurosoftit.ebok.web.rest.vacation.service.ApproveLevelType;
import com.eurosoftit.ebok.web.rest.vacation.service.NotificationValidator;
import com.eurosoftit.ebok.web.rest.vacation.service.VacationErpService;
import com.google.common.collect.Sets;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.BooleanUtils;
import org.hibernate.ObjectNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.eurosoftit.ebok.domain.enumeration.NotificationCategory.VACATION;

@Api
@RestController
@RequestMapping("/api/admin/vacation")
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")" + " OR " + "hasAuthority(\"" + AuthoritiesConstants.SYSTEM + "\")" + " OR " + "hasAuthority(\"" + AuthoritiesConstants.HR + "\")")
class VacationAdminResource {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final Logger log = LoggerFactory.getLogger(VacationAdminResource.class);
    private static final String ENTITY_NAME = "vacation";

    @Value("${smsapi.url:''}")
    private String smsapiUrl;

    @Value("${vacation.approve:ONE_LEVEL}")
    private ApproveLevelType approveLevel;

    @Autowired
    private VacationService vacationService;
    @Autowired
    private NotificationValidator notificationValidator;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserNotificationSender userNotificationSender;
    @Autowired
    private VacationErpService vacationErpService;

    @Value("${vacation.approve:ONE_LEVEL}")
    private String approveDefinition;

    @GetMapping("/statusType")
    List<DictionaryDTO> getDictionary() {
        return vacationErpService.getStatusType().stream()
            .map(value -> new DictionaryDTO(Long.valueOf(value.ordinal()), value.name())).collect(Collectors.toList());
    }

    @GetMapping(value = "/statusType/default", produces = MediaType.TEXT_PLAIN_VALUE)
    String getDefaultDictionary() {
        return vacationErpService.getDefaultStatusType();
    }

    @GetMapping(value = "/approve/definition", produces = MediaType.TEXT_PLAIN_VALUE)
    String getApproveDefinition() {
        return this.approveDefinition;
    }

    @DeleteMapping(value = "/{id}")
    @Transactional
    ResponseEntity<?> delete(@PathVariable("id") Long id) {
        log.debug("REST request to delete vacation");

        Vacation vacation = vacationService.findOne(id).orElseThrow(() -> new ObjectNotFoundException(id, "vacation not exists"));

        if (!VacationStatus.WAITING.equals(vacation.getStatus())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        vacationService.delete(vacation);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Transactional
    Page<Vacation> get(VacationAdminProperties vacationProperties, Pageable pageable) {
        log.debug("REST request to get vacation");

        if (!userUtils.hasRole(AuthoritiesConstants.HR)) {
            final List<Long> users = getApprovedUserIds(vacationProperties);
            vacationProperties.setRelatedUsers(users);
        }

        return this.vacationService.findAll(vacationProperties, pageable);
    }

    private List<Long> getApprovedUserIds(VacationAdminProperties vacationProperties) {
        final List<Long> users = new ArrayList<>();
        if (approveLevel == ApproveLevelType.TWO_LEVEL) {
            final ApprovalType userApprovalType = userService.getUserApprovalType(userUtils.getLoggedUserId());
            if (userApprovalType == ApprovalType.INITIAL_VERIFICATION) {
                List<Long> tempUsers = new ArrayList<>();
                tempUsers.addAll(this.userUtils.getLoggedUserRelatedUserIds());
                tempUsers.add(this.userUtils.getLoggedUserId());
                users.addAll(this.userService.getTwoLevelApproval(tempUsers));
            } else if (userApprovalType == ApprovalType.FINAL_VERIFICATION) {
                users.addAll(this.userUtils.getLoggedUserRelatedUserIds());
                users.add(this.userUtils.getLoggedUserId());
                //ALBO ONE_LEVEL, ALBO ACCEPTED INITIALLY
                vacationProperties.setVacationIds(vacationService.findIdsWithStatusVacationWaitingAndTwoLevelAndInitialApproval(users));
            } else {
                users.addAll(this.userUtils.getLoggedUserRelatedUserIds());
                users.add(this.userUtils.getLoggedUserId());
            }
        } else {
            users.addAll(this.userUtils.getLoggedUserRelatedUserIds());
            users.add(this.userUtils.getLoggedUserId());
        }
        return users;
    }

    @GetMapping("/user-approval-verification-requirement")
    @Transactional
    String getUserApprovalVerificationRequirement() {
        log.debug("REST request to user approval verification requirement");
        return userService.getUserApprovalVerificationRequirement(userUtils.getLoggedUserId());
    }

    @GetMapping("/user-approval")
    @Transactional
    String getUserApproval() {
        log.debug("REST request to user approval");
        ApprovalType userApprovalType = userService.getUserApprovalType(userUtils.getLoggedUserId());
        if (userApprovalType == null) {
            return null;
        }
        return userApprovalType.name();
    }

    @PostMapping
    @Transactional
    void save(@RequestBody @Valid Vacation vacation) {
        log.debug("REST request to save vacation");

        vacationService.isValid(vacation);
        vacation.setCreatedDate(ZonedDateTime.now());
        vacation.setCreatedBy(userUtils.getLoggerUserReference());
        vacation.setStatus(VacationStatus.WAITING);
        vacationService.save(vacation);
    }

    @PutMapping
    @Transactional
    void modify(@RequestBody @Valid Vacation vacation) {
        log.debug("REST request to save vacation");
        if (vacation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (VacationStatus.CANCELLED.equals(vacation.getStatus()) && !userUtils.hasRole(AuthoritiesConstants.HR)) {
            throw new AccessDeniedException("Only HR can withdraw a vacation");
        }
        if (VacationStatus.ACCEPTED.equals(vacation.getStatus()) && vacation.getCreatedBy() != null) {
            Long applicantExternalId = userRepository.findById(vacation.getCreatedBy().getId()).map(User::getExternalId).orElse(null);
            if (applicantExternalId != null && !vacationService.hasEnoughForAcceptance(applicantExternalId, vacation)) {
                throw new BadRequestAlertException("Not enough vacation days", ENTITY_NAME, "notEnoughDays");
            }
        }
        userRepository.findById(userUtils.getLoggedUserId()).ifPresent(u -> vacation.setProcessedBy(u));
        notificationValidator.validate(vacation);

        if (!(VacationStatus.ACCEPTED_INITIALLY.equals(vacation.getStatus()))) {

            Notification notification = new Notification();
            notification.setCategory(VACATION);

            if (Objects.nonNull(vacation.getStatus())) {
                String action;
                if (VacationStatus.ACCEPTED.equals(vacation.getStatus())) {
                    action = "zaakceptowany";
                } else if (VacationStatus.CANCELLED.equals(vacation.getStatus())) {
                    action = "wycofany";
                } else {
                    action = "odrzucony";
                }
                notification.setSubject("Wniosek urlopowy z dnia: " + vacation.getCreatedDate().format(formatter) + " - " + action);
            }

            notification.setDescription(vacation.getStatusDescription());
            notification.setCreateDate(ZonedDateTime.now());
            userRepository.findById(vacation.getCreatedBy().getId()).ifPresent(u -> notification.setCreatedBy(u));
            notification.setRecepients(Sets.newHashSet(vacation.getCreatedBy()));

            notificationService.save(notification);

            final User createdBy = notification.getCreatedBy();
            log.debug("Operacja " + vacation.getStatus());
            log.debug("User " + createdBy);
            if (createdBy != null && createdBy.getActivated()) {
                log.debug("Send by Email" + BooleanUtils.isTrue(notification.getSendMail()));
                log.debug("Send by SMS" + BooleanUtils.isTrue(notification.getSendSms()));
                if (BooleanUtils.isTrue(notification.getSendMail())) {
                    this.userNotificationSender.sendEmail(Arrays.asList(createdBy.getEmail()), "e_Pracownik: " + notification.getSubject() + ". Powód: " + vacation.getStatusDescription(), "e_Pracownik - wniosek urlopowy");
                } else if (BooleanUtils.isTrue(notification.getSendSms())) {
                    this.userNotificationSender.sendSms(Arrays.asList(createdBy.getPhoneNumber()), notification.getSubject() + ". Powód: " + vacation.getStatusDescription());
                } else {
                    List<String> identifiers = getIdentifiers(createdBy);
                    this.userNotificationSender.send(identifiers, notification.getSubject() + ". Powód: " + vacation.getStatusDescription(), "wniosek urlopowy");
                }
            }

            if (VacationStatus.CANCELLED.equals(vacation.getStatus())) {
                vacationErpService.removeFromErp(vacation);
            } else {
                vacationErpService.writeToErp(vacation);
            }
        }

        vacationService.save(vacation);
    }

    private List<String> getIdentifiers(User createdBy) {
        return Arrays.asList(this.userNotificationSender.getNotificationType() == UserNotificationSender.NotificationType.SMS ? createdBy.getPhoneNumber() : createdBy.getEmail());
    }
}
