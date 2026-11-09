package com.eurosoftit.ebok.web.rest.integration;

import com.eurosoftit.ebok.config.Constants;
import com.eurosoftit.ebok.domain.User;
import com.eurosoftit.ebok.service.MailService;
import com.eurosoftit.ebok.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserCreator {
    private static Pattern EMAIL = Pattern.compile("^[a-zA-Z0-9!#$%&'*+\\/=?^_`{|}~.-]+@[a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])?(\\.[a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])?)*$");
    private static Pattern LOGIN = Pattern.compile(Constants.LOGIN_REGEX);

    @Autowired
    private MailService mailService;
    @Autowired
    private UserService userService;

    UserCreationResult createUsers(List<UserDTO> users) {
        UserCreationResult result = new UserCreationResult();
        users.forEach(u -> createUser(u, result));
        return result;
    }

    List<IntegrationCandidateDTO> preview(List<UserDTO> users) {
        return users.stream()
            .map(u -> new IntegrationCandidateDTO(u.getId(), u.getFirstName(), u.getLastName(), u.getEmail(), u.getType(), problem(u)))
            .collect(Collectors.toList());
    }

    private void createUser(UserDTO userDTO, UserCreationResult result) {
        String problem = problem(userDTO);
        if (problem != null) {
            result.getErrorOrCreate().add(new UserWithErrorDTO(userDTO, problem));
        } else {
            userService.createUser(userDTO);
            result.getSuccess().add(userDTO);
        }
    }

    private String problem(UserDTO userDTO) {
        String email = StringUtils.trim(userDTO.getEmail());
        if (StringUtils.isBlank(email)) {
            return "emailIsEmpty";
        }
        if (!EMAIL.matcher(email).find()) {
            return "emailWrongPattern";
        }
        if (StringUtils.isBlank(userDTO.getFirstName())) {
            return "wrongLogin";
        }
        if (userService.userExists(normalize(userDTO.getFirstName()) + userDTO.getId())) {
            return "loginAlreadyUsed";
        }
        if (userService.userExistsByEmail(email)) {
            return "emailAlreadyUsed";
        }
        return null;
    }

    private String normalize(String value) {
        String[] original =new String[]{"Ą", "ą", "Ć", "ć", "Ę", "ę", "Ł", "ł", "Ń", "ń", "Ó", "ó", "Ś", "ś", "Ź", "ź", "Ż", "ż"};
        String[] normalized =new String[]{"A", "a", "C", "c", "E", "e", "L", "l", "N", "n", "O", "o", "S", "s", "Z", "z", "Z", "z"};


        for (int i = 0; i < original.length; i++) {
            value = value.replace(original[i],normalized[i]);
        }

        return value.toLowerCase();
    }

    @Transactional
    public UserCreationResult resendSelected(List<Long> ids) {
        UserCreationResult result = new UserCreationResult();
        if (ids == null || ids.isEmpty()) {
            return result;
        }

        List<Long> toSend = new ArrayList<>();
        for (User user : userService.findAll(ids)) {
            String problem = resendProblem(user);
            if (problem != null) {
                result.getErrorOrCreate().add(new UserWithErrorDTO(toDTO(user), problem));
            } else {
                userService.refreshActivationKey(user);
                toSend.add(user.getId());
            }
        }

        UserCreationResult sent = resend(toSend);
        result.getSuccess().addAll(sent.getSuccess());
        if (sent.getError() != null) {
            result.getErrorOrCreate().addAll(sent.getError());
        }
        return result;
    }

    static String resendProblem(User user) {
        if (Boolean.TRUE.equals(user.getActivated())) {
            return "alreadyActivated";
        }
        if (StringUtils.isBlank(user.getEmail())) {
            return "emailIsEmpty";
        }
        return null;
    }

    private static UserDTO toDTO(User user) {
        return new UserDTO(user.getExternalId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getType());
    }

    @Transactional
    public UserCreationResult resendOne(Long userId) {
        User user = userService.findAll(Collections.singletonList(userId)).stream().findFirst()
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        if (user.getActivated()) {
            throw new IllegalStateException("User already activated: " + userId);
        }
        userService.refreshActivationKey(user);
        return resend(Collections.singletonList(userId));
    }

    @Transactional
    public UserCreationResult resend(List<Long> ids) {
        UserCreationResult result = new UserCreationResult();

        List<User> all = userService.findAll(ids);
        for (User user : all) {
            try {
                mailService.sendCreationEmail(user);
                result.getSuccess().add(new UserDTO(user.getExternalId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getType()));
            } catch (Exception e) {
                result.getErrorOrCreate().add(new UserWithErrorDTO(new UserDTO(user.getExternalId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getType()), "errorWhileSendingEmail"));
            }
        }

        return result;
    }
}
