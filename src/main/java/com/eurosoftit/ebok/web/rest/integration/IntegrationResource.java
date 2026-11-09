package com.eurosoftit.ebok.web.rest.integration;

import com.eurosoftit.ebok.security.AuthoritiesConstants;
import com.eurosoftit.ebok.service.UserService;
import lombok.Data;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.Collator;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
class IntegrationResource {

    private final Logger log = LoggerFactory.getLogger(IntegrationResource.class);

    @Autowired
    private PersonNotInSystemRepository personNotInSystemRepository;
    @Autowired
    private UserCreator createUser;
    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")" + " OR " + "hasAuthority(\"" + AuthoritiesConstants.SYSTEM + "\")")
    @PostMapping("/updateUserDatabase")
    @Transactional
    UserCreationResult updateUserDatabase(@RequestBody(required = false) SyncProperties syncProperties){
        log.debug("REST request to update users");

        boolean contractor = syncProperties != null && BooleanUtils.isTrue(syncProperties.getContractor());
        List<UserDTO> candidates = personNotInSystemRepository.findAll(types(contractor), ZonedDateTime.now());

        if (syncProperties != null && syncProperties.getExternalIds() != null) {
            Set<Long> selected = new HashSet<>(syncProperties.getExternalIds());
            candidates = candidates.stream().filter(c -> selected.contains(c.getId())).collect(Collectors.toList());
        }

        return createUser.createUsers(candidates);
    }

    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")" + " OR " + "hasAuthority(\"" + AuthoritiesConstants.SYSTEM + "\")")
    @GetMapping("/integration/candidates")
    @Transactional(readOnly = true)
    List<IntegrationCandidateDTO> candidates(@RequestParam(value = "contractor", defaultValue = "false") boolean contractor) {
        log.debug("REST request to preview users to integrate");
        Collator collator = Collator.getInstance(new Locale("pl", "PL"));
        return createUser.preview(personNotInSystemRepository.findAll(types(contractor), ZonedDateTime.now())).stream()
            .sorted(Comparator.comparing(IntegrationCandidateDTO::getLastName, Comparator.nullsLast(collator::compare))
                .thenComparing(IntegrationCandidateDTO::getFirstName, Comparator.nullsLast(collator::compare)))
            .collect(Collectors.toList());
    }

    private List<String> types(boolean contractor) {
        final List<String> types = new ArrayList<>();
        types.add("P");
        if (contractor) {
            types.add("O");
        }
        return types;
    }

    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")" + " OR " + "hasAuthority(\"" + AuthoritiesConstants.SYSTEM + "\")")
    @GetMapping("/activation/resend")
    UserCreationResult resend(){
        log.debug("REST request to update users");

        final List<Long> notActivated = userService.findNotActivated();
        userService.resetPasswordResetProcessForExpired(notActivated);
        return createUser.resend(notActivated);
    }

    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")" + " OR " + "hasAuthority(\"" + AuthoritiesConstants.SYSTEM + "\")")
    @PostMapping("/activation/resend")
    UserCreationResult resendSelected(@RequestBody List<Long> userIds) {
        log.debug("REST request to send activation e-mails to users {}", userIds);
        return createUser.resendSelected(userIds);
    }

    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")" + " OR " + "hasAuthority(\"" + AuthoritiesConstants.SYSTEM + "\")")
    @PostMapping("/activation/resend/{userId}")
    ResponseEntity<UserCreationResult> resendOne(@PathVariable("userId") Long userId) {
        log.debug("REST request to resend activation e-mail to user {}", userId);
        try {
            return ResponseEntity.ok(createUser.resendOne(userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @Data
    static class SyncProperties {
        private Boolean contractor;
        private List<Long> externalIds;
    }
}
