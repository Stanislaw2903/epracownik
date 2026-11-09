package com.eurosoftit.ebok.web.rest.integration;

import com.eurosoftit.ebok.security.AuthoritiesConstants;
import com.eurosoftit.ebok.service.UserService;
import lombok.Data;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

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

        final List<String> types = new ArrayList<>();
        types.add("P");

        if(syncProperties != null && BooleanUtils.isTrue(syncProperties.getContractor())) {
            types.add("O");
        }

        return createUser.createUsers(personNotInSystemRepository.findAll(types, ZonedDateTime.now()));
    }

    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")" + " OR " + "hasAuthority(\"" + AuthoritiesConstants.SYSTEM + "\")")
    @GetMapping("/activation/resend")
    UserCreationResult resend(){
        log.debug("REST request to update users");

        final List<Long> notActivated = userService.findNotActivated();
        userService.resetPasswordResetProcessForExpired(notActivated);
        return createUser.resend(notActivated);
    }

    @Data
    static class SyncProperties {
        private Boolean contractor;
    }
}
