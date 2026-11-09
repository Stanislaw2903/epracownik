package com.eurosoftit.ebok.web.rest.vacation;

import com.eurosoftit.ebok.service.NotificationService;
import com.eurosoftit.ebok.service.UserNotificationSender;
import com.eurosoftit.ebok.service.UserService;
import com.eurosoftit.ebok.web.rest.UserUtils;
import com.eurosoftit.ebok.web.rest.integration.UserNameDTO;
import io.swagger.annotations.Api;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@Api
@RestController
@RequestMapping("/api/vacation")
class VacationResource {
    private final Logger log = LoggerFactory.getLogger(VacationResource.class);
    private static final String ENTITY_NAME = "vacation";

    @Autowired
    private VacationService vacationService;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private UserService userService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserNotificationSender userNotificationSender;

    @Value("${smsapi.url:''}")
    private String smsapiUrl;

    @GetMapping(value = "/{id}")
    @Transactional
    Vacation get(@PathVariable("id") Long id) {
        log.debug("REST request to get vacation");
        return vacationService.findOne(id).orElseThrow(() -> new ObjectNotFoundException(id, "vacation not exists"));
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
    Page<Vacation> get(VacationProperties vacationProperties, Pageable pageable) {
        log.debug("REST request to get vacation");

        VacationAdminProperties vacationAdminProperties = vacationProperties.toAdmin();
        vacationAdminProperties.setVacationType(vacationProperties.getVacationType());
        vacationAdminProperties.setUserId(userUtils.getLoggedUserId());

        List<Long> users = new ArrayList<>();
        users.addAll(userUtils.getLoggedUserRelatedUserIds());
        users.add(vacationAdminProperties.getUserId());
        vacationAdminProperties.setRelatedUsers(users);
        return vacationService.findAll(vacationAdminProperties, pageable);
    }

    @PostMapping
    @Transactional
    ResponseEntity<?> save(@RequestBody @Valid Vacation vacation) throws VacationException {
        log.debug("REST request to save vacation");

        try {
            vacationService.isValid(vacation);
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }

        vacation.setCreatedDate(ZonedDateTime.now());
        vacation.setCreatedBy(userUtils.getLoggerUserReference());
        vacation.setStatus(VacationStatus.WAITING);
        vacationService.save(vacation);

        final UserNameDTO userDTO = userService.findOneDTO(userUtils.getLoggedUserId());
        String content = userDTO.getName() + " złożył wniosek o urlop";

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        if (vacation.getDateFrom() != null) {
            if (vacation.getDateTo() != null) {
                content = content + " na okres " + vacation.getDateFrom().format(formatter) + " - " + vacation.getDateTo().format(formatter);
            } else {
                content = content + " od dnia " + vacation.getDateFrom().format(formatter);
            }
        }

        this.userNotificationSender.send(getIdentifiers(), content, "wniosek urlopowy");

        return ResponseEntity.ok().build();
    }

    private List<String> getIdentifiers() {
        List<String> identifiers = new ArrayList<>();
        switch (this.userNotificationSender.getNotificationType()) {
            case LOCAL_MAIL:
            case MAIL:
                identifiers.addAll(userService.findUserEmailsForRelationGroup(userUtils.getLoggedUserGroupId()));
                identifiers.addAll(userService.findUserEmailsForRelation(userUtils.getLoggedUserId()));
                break;
            case SMS:
                identifiers.addAll(userService.findUserPhoneForRelationGroup(userUtils.getLoggedUserGroupId()));
                identifiers.addAll(userService.findUserPhoneForRelation(userUtils.getLoggedUserId()));
                break;
        }

        return identifiers.stream().distinct().collect(Collectors.toList());
    }

    @GetMapping("/types")
    public List<String> types() {
        return Arrays.stream(VacationType.values()).map(VacationType::name).collect(Collectors.toList());
    }
}
