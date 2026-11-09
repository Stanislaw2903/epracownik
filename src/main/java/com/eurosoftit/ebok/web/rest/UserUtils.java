package com.eurosoftit.ebok.web.rest;

import com.eurosoftit.ebok.config.Constants;
import com.eurosoftit.ebok.domain.User;
import com.eurosoftit.ebok.repository.UserRepository;
import com.eurosoftit.ebok.security.SecurityUtils;
import com.eurosoftit.ebok.service.dto.AuthorizationToAcceptVacationType;
import com.eurosoftit.ebok.web.rest.errors.NotificationResourceException;
import com.google.common.base.Function;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserUtils {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    public Long getLoggedUserId() {
        String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new NotificationResourceException("Current user login not found"));
        log.debug("found login {}", userLogin);
        Long userId = userRepository.findIdByLogin(userLogin).orElseThrow(() -> new NotificationResourceException("User could not be found"));
        log.debug("found userId {}", userId);
        return userId;
    }

    public Long getLoggedUserGroupId() {
        String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new NotificationResourceException("Current user login not found"));
        log.debug("found login {}", userLogin);
        Long groupId = userRepository.findGroupIdByLogin(userLogin);
        log.debug("found groupId {}", groupId);
        return groupId;
    }

    public Long getExternalUserId() {
        String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new NotificationResourceException("Current user login not found"));
        Long userId = userRepository.findExternalIdByLogin(userLogin).orElse(-1l);
        return userId;
    }

    public User getLoggerUserReference() {
        return entityManager.getReference(User.class, getLoggedUserId());
    }

    public User getLoggerUser() {
        return userRepository.getOne(getLoggedUserId());
    }

    public List<Long> getLoggedUserRelatedUserIds() {
        Long loggedUserId = getLoggedUserId();

        AuthorizationToAcceptVacationType authorizationToAcceptVacationType = userRepository.findAuthorizationToAcceptVacationType(loggedUserId);

        if (AuthorizationToAcceptVacationType.ALL.equals(authorizationToAcceptVacationType)) {
            return userRepository.findAllWithout(Constants.ANONYMOUS_USER).stream().map(User::getId).collect(Collectors.toList());
        } else if (AuthorizationToAcceptVacationType.SPECIFIC_GROUP.equals(authorizationToAcceptVacationType)) {
            return userRepository.findRelatedUserIdsByUserId(loggedUserId);
        } else if (AuthorizationToAcceptVacationType.USER_GROUP.equals(authorizationToAcceptVacationType)) {
            final List<Long> groups = userRepository.findGroups(loggedUserId);
            if (CollectionUtils.isEmpty(groups)) {
                return new ArrayList<>();
            }

            return userRepository.findByGroupIds(groups).stream().map(User::getId).collect(Collectors.toList());
        } else if (AuthorizationToAcceptVacationType.NO_ONE.equals(authorizationToAcceptVacationType)) {
            List<Long> ids = new ArrayList<>();
            ids.add(-1l);
            return ids;
        }

        return Lists.newArrayList();
    }

    public List<Long> getAllUsersAndExtract(Function<User, Long> extractor) {
        return userRepository.findAllWithout(Constants.ANONYMOUS_USER).stream().map(extractor).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<Long> getLoggedUserRelatedUserAndExtract(Function<User, Long> extractor) {
        Long loggedUserId = getLoggedUserId();

        AuthorizationToAcceptVacationType authorizationToAcceptVacationType = userRepository.findAuthorizationToAcceptVacationType(loggedUserId);

        if (AuthorizationToAcceptVacationType.ALL.equals(authorizationToAcceptVacationType)) {
            return userRepository.findAllWithout(Constants.ANONYMOUS_USER).stream().map(extractor).filter(Objects::nonNull).collect(Collectors.toList());
        } else if (AuthorizationToAcceptVacationType.SPECIFIC_GROUP.equals(authorizationToAcceptVacationType)) {
            return userRepository.findRelatedUserByUserId(loggedUserId).stream().map(extractor).filter(Objects::nonNull).collect(Collectors.toList());
        } else if (AuthorizationToAcceptVacationType.USER_GROUP.equals(authorizationToAcceptVacationType)) {
            final List<Long> groups = userRepository.findGroups(loggedUserId);
            if (CollectionUtils.isEmpty(groups)) {
                return new ArrayList<>();
            }

            return userRepository.findByGroupIds(groups).stream().map(extractor).filter(Objects::nonNull).collect(Collectors.toList());
        }

        return Lists.newArrayList();
    }


    public List<Long> getLoggedNativeUserRelatedUserIds() {
        Long loggedUserId = getLoggedUserId();

        AuthorizationToAcceptVacationType authorizationToAcceptVacationType = userRepository.findAuthorizationToAcceptVacationType(loggedUserId);

        if (AuthorizationToAcceptVacationType.ALL.equals(authorizationToAcceptVacationType)) {
            log.debug("ALL");
            return userRepository.findNativeAllWithout(Constants.ANONYMOUS_USER).stream()
                .map(BigInteger::longValue)
                .collect(Collectors.toList());
        } else if (AuthorizationToAcceptVacationType.SPECIFIC_GROUP.equals(authorizationToAcceptVacationType)) {
            log.debug("SPECIFIC_GROUP");
            return userRepository.findNativeRelatedUserIdsByUserId(loggedUserId).stream()
                .map(BigInteger::longValue)
                .collect(Collectors.toList());
        } else if (AuthorizationToAcceptVacationType.USER_GROUP.equals(authorizationToAcceptVacationType)) {
            log.debug("USER_GROUP");
            List<Long> groups = userRepository.findGroups(loggedUserId);
            return userRepository.findUserByGroupsViewByGroupIds(groups).stream()
                .map(BigInteger::longValue)
                .collect(Collectors.toList());
        } else if (AuthorizationToAcceptVacationType.NO_ONE.equals(authorizationToAcceptVacationType)) {
            List<Long> ids = new ArrayList<>();
            ids.add(-1l);
            return ids;
        }

        return new ArrayList<>();
    }

    public List<Long> getLoggedUserAndRelatedUserIds() {
        List<Long> users = new ArrayList<>();
        List<Long> related = getLoggedNativeUserRelatedUserIds();
        if (!CollectionUtils.isEmpty(related)) {
            users.addAll(related);
        }
        users.add(getLoggedUserId());
        return users;
    }

    public boolean hasRole(String role) {
        final Long loggedUserId = getLoggedUserId();
        return userRepository.hasRole(role, loggedUserId);
    }
}
