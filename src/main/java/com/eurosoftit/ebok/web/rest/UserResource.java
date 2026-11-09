package com.eurosoftit.ebok.web.rest;

import com.eurosoftit.ebok.domain.User;
import com.eurosoftit.ebok.domain.specification.SpecificationReflectionBuilder;
import com.eurosoftit.ebok.repository.UserRepository;
import com.eurosoftit.ebok.security.AuthoritiesConstants;
import com.eurosoftit.ebok.service.MailService;
import com.eurosoftit.ebok.service.UserService;
import com.eurosoftit.ebok.service.dto.UserDTO;
import com.eurosoftit.ebok.web.rest.errors.BadRequestAlertException;
import com.eurosoftit.ebok.web.rest.errors.EmailAlreadyUsedException;
import com.eurosoftit.ebok.web.rest.errors.ExternalIdAlreadyUsedException;
import com.eurosoftit.ebok.web.rest.errors.LoginAlreadyUsedException;
import com.eurosoftit.ebok.web.rest.group.UserGroupEntity;
import com.eurosoftit.ebok.web.rest.group.UserGroupParams;
import com.eurosoftit.ebok.web.rest.group.UserGroupRepository;
import com.eurosoftit.ebok.web.rest.integration.SimpleUserDTO;
import com.eurosoftit.ebok.web.rest.integration.UserNameDTO;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing users.
 * <p>
 * This class accesses the {@link User} entity, and needs to fetch its collection of authorities.
 * <p>
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no View Model and DTO, a lot less code, and an outer-join
 * which would be good for performance.
 * <p>
 * We use a View Model and a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.</li>
 * <li> Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li> As this manages users, for security reasons, we'd rather have a DTO layer.</li>
 * </ul>
 * <p>
 * Another option would be to have a specific JPA entity graph to handle this case.
 */
@RestController
@RequestMapping("/api")
public class UserResource {
    private static final List<String> ALLOWED_ORDERED_PROPERTIES = Collections.unmodifiableList(Arrays.asList("id", "login", "firstName", "lastName", "email", "externalId", "activated", "langKey", "createdDate"));

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Value("${activity.url}")
    private String activityUrl;

    private final UserService userService;

    private final UserRepository userRepository;

    private final UserGroupRepository groupRepository;

    private final MailService mailService;

    private final UserUtils userUtils;

    private final ActivityClient activityClient;

    public UserResource(UserService userService, UserRepository userRepository, MailService mailService, UserUtils userUtils, UserGroupRepository groupRepository, ActivityClient activityClient) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.userUtils = userUtils;
        this.groupRepository = groupRepository;
        this.activityClient = activityClient;
    }

    /**
     * {@code POST  /users}  : Creates a new user.
     * <p>
     * Creates a new user if the login and email are not already used, and sends an
     * mail with an activation link.
     * The user needs to be activated on creation.
     *
     * @param userDTO the user to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws URISyntaxException       if the Location URI syntax is incorrect.
     * @throws BadRequestAlertException {@code 400 (Bad Request)} if the login or email is already in use.
     */
    @PostMapping("/users")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")" + " OR " + "hasAuthority(\"" + AuthoritiesConstants.SYSTEM + "\")")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDTO userDTO) throws URISyntaxException {
        log.debug("REST request to save User : {}", userDTO);

        if (userDTO.getId() != null) {
            throw new BadRequestAlertException("A new user cannot already have an ID", "userManagement", "idexists");
            // Lowercase the user login before comparing with database
        } else if (userRepository.findOneByLogin(normalize(userDTO.getLogin().toLowerCase())).isPresent()) {
            throw new LoginAlreadyUsedException();
        } else if (userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException();
        } else if (userRepository.userExistsByExternalId(userDTO.getExternalId())) {
            throw new ExternalIdAlreadyUsedException();
        } else {
            User newUser = userService.createUser(userDTO);
            mailService.sendCreationEmail(newUser);
            return ResponseEntity.created(new URI("/api/users/" + newUser.getLogin()))
                .headers(HeaderUtil.createAlert(applicationName, "userManagement.created", newUser.getLogin()))
                .body(newUser);
        }
    }

    @GetMapping("/users/groups")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")" + " OR " + "hasAuthority(\"" + AuthoritiesConstants.SYSTEM + "\")")
    public ResponseEntity<List<UserGroupEntity>> groups(UserGroupParams params, Pageable pageable) throws URISyntaxException {
        Specification<UserGroupEntity> specification = SpecificationReflectionBuilder.process(params);
        Page<UserGroupEntity> page = groupRepository.findAll(specification, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @Transactional
    @GetMapping("/users/activity-url")
    public FakeObject activity() {
        final User loggerUserReference = userUtils.getLoggerUserReference();

        final ActivityClient.IdVendo idVendo;

        try {
            idVendo = this.activityClient.getId(loggerUserReference.getExternalId(), loggerUserReference.getFirstName() + " " + loggerUserReference.getLastName());
        } catch (Exception exception) {
            throw new UnsupportedOperationException();
        }

        return new FakeObject(activityUrl + "/summary.php?id=" + idVendo.idvendo + "&userName=" + loggerUserReference.getFirstName() + " " + loggerUserReference.getLastName());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class FakeObject {
        private String value;
    }

    @Transactional
    @GetMapping("/users/activity-url/active")
    public ResponseEntity<Boolean> activityActive() {
        final User loggerUserReference = userUtils.getLoggerUserReference();
        final ActivityClient.IdVendo idVendo;

        try {
            idVendo = this.activityClient.getId(loggerUserReference.getExternalId(), loggerUserReference.getFirstName() + " " + loggerUserReference.getLastName());
        } catch (Exception exception) {
            return new ResponseEntity<>(false, HttpStatus.OK);
        }

        return new ResponseEntity<>(idVendo.status == 200, HttpStatus.OK);
    }

    private String normalize(String value) {
        String[] original = new String[]{"Ą", "ą", "Ć", "ć", "Ę", "ę", "Ł", "ł", "Ń", "ń", "Ó", "ó", "Ś", "ś", "Ź", "ź", "Ż", "ż"};
        String[] normalized = new String[]{"A", "a", "C", "c", "E", "e", "L", "l", "N", "n", "O", "o", "S", "s", "Z", "z", "Z", "z"};


        for (int i = 0; i < original.length; i++) {
            value = value.replace(original[i], normalized[i]);
        }

        return value.toLowerCase();
    }

    /**
     * {@code PUT /users} : Updates an existing User.
     *
     * @param userDTO the user to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated user.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already in use.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already in use.
     */
    @PutMapping("/users")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")" + " OR " + "hasAuthority(\"" + AuthoritiesConstants.SYSTEM + "\")")
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserDTO userDTO) {
        log.debug("REST request to update User : {}", userDTO);
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
            throw new EmailAlreadyUsedException();
        }
        existingUser = userRepository.findOneByLogin(normalize(userDTO.getLogin()).toLowerCase());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
            throw new LoginAlreadyUsedException();
        }

        if (userRepository.userExistsByExternalId(userDTO.getExternalId(), userDTO.getId())) {
            throw new ExternalIdAlreadyUsedException();
        }

        Optional<UserDTO> updatedUser = userService.updateUser(userDTO);

        return ResponseUtil.wrapOrNotFound(updatedUser,
            HeaderUtil.createAlert(applicationName, "userManagement.updated", userDTO.getLogin()));
    }

    /**
     * {@code GET /users} : get all users.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers(UserProperties properties, Pageable pageable) {
        if (!onlyContainsAllowedProperties(pageable)) {
            return ResponseEntity.badRequest().build();
        }

        final Page<UserDTO> page = userService.getAllManagedUsers(properties, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping(value = "/users", params = "active")
    public ResponseEntity<List<UserDTO>> getAllReceivers(UserProperties properties, Pageable pageable) {
        if (!onlyContainsAllowedProperties(pageable)) {
            return ResponseEntity.badRequest().build();
        }

        final Page<UserDTO> page = userService.getAllActivedManagedUsers(properties, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping(value = "/users", params = "related")
    public ResponseEntity<List<UserDTO>> getRelatedUsers(@PathParam("related") Long related, Pageable pageable) {
        if (!onlyContainsAllowedProperties(pageable)) {
            return ResponseEntity.badRequest().build();
        }

        log.debug("Get related users for {}", related);
        final Page<UserDTO> page = userService.findRelatedUsers(related, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping(value = "/users", params = "withPhone")
    public ResponseEntity<List<UserDTO>> getAllWithPhone(Pageable pageable) {
        if (!onlyContainsAllowedProperties(pageable)) {
            return ResponseEntity.badRequest().build();
        }

        log.debug("Get users with phone ");
        final Page<UserDTO> page = userService.findUsersWithPhone(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    private boolean onlyContainsAllowedProperties(Pageable pageable) {
        return pageable.getSort().stream().map(Sort.Order::getProperty).allMatch(ALLOWED_ORDERED_PROPERTIES::contains);
    }

    /**
     * Gets a list of all roles.
     *
     * @return a string list of all roles.
     */
    @GetMapping("/users/authorities")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")" + " OR " + "hasAuthority(\"" + AuthoritiesConstants.SYSTEM + "\")")
    public List<String> getAuthorities() {
        return userService.getAuthorities();
    }

    /**
     * {@code GET /users/:login} : get the "login" user.
     *
     * @param login the login of the user to find.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the "login" user, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/users/{login}")
    @Transactional
    public ResponseEntity<UserDTO> getUser(@PathVariable String login) {
        log.debug("REST request to get User : {}", login);
        return ResponseUtil.wrapOrNotFound(
            userService.getUserWithAuthoritiesByLogin(login)
                .map(UserDTO::new));
    }

    /**
     * {@code DELETE /users/:login} : delete the "login" User.
     *
     * @param login the login of the user to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/users/{login}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")" + " OR " + "hasAuthority(\"" + AuthoritiesConstants.SYSTEM + "\")")
    public ResponseEntity<Void> deleteUser(@PathVariable String login) {
        log.debug("REST request to delete User: {}", login);
        userService.deleteUser(login);
        return ResponseEntity.noContent().headers(HeaderUtil.createAlert(applicationName, "userManagement.deleted", login)).build();
    }


    @GetMapping(value = "/users/relatedUsers/{id}")
    public List<SimpleUserDTO> relatedUsers(@PathVariable Long id) {
        return userService.findUserForRelation(id);
    }

    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")" + " OR " + "hasAuthority(\"" + AuthoritiesConstants.SYSTEM + "\")")
    @GetMapping(value = "/users/all")
    public List<UserNameDTO> getAll() {
        return userService.findAllDTO();
    }
}
