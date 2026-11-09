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
import java.util.List;
import java.util.regex.Pattern;

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

    private void createUser(UserDTO userDTO, UserCreationResult result) {

        String login = normalize(userDTO.getFirstName()) + userDTO.getId();
        String email = StringUtils.trim(userDTO.getEmail());
        if (StringUtils.isBlank(email)) {
            result.getErrorOrCreate().add(new UserWithErrorDTO(userDTO, "emailIsEmpty"));
        } else if (!EMAIL.matcher(email).find()) {
            result.getErrorOrCreate().add(new UserWithErrorDTO(userDTO, "emailWrongPattern"));
        } else if (userService.userExists(login)) {
            result.getErrorOrCreate().add(new UserWithErrorDTO(userDTO, "loginAlreadyUsed"));
        } else if (userService.userExistsByEmail(email)) {
            result.getErrorOrCreate().add(new UserWithErrorDTO(userDTO, "emailAlreadyUsed"));
        } else {
            User newUser = userService.createUser(userDTO);
            send(userDTO, result, newUser);
        }
    }

    private String normalize(String value) {
        String[] original =new String[]{"Ą", "ą", "Ć", "ć", "Ę", "ę", "Ł", "ł", "Ń", "ń", "Ó", "ó", "Ś", "ś", "Ź", "ź", "Ż", "ż"};
        String[] normalized =new String[]{"A", "a", "C", "c", "E", "e", "L", "l", "N", "n", "O", "o", "S", "s", "Z", "z", "Z", "z"};


        for (int i = 0; i < original.length; i++) {
            value = value.replace(original[i],normalized[i]);
        }

        return value.toLowerCase();
    }

    private void send(UserDTO userDTO, UserCreationResult result, User newUser) {
        try {
            mailService.sendCreationEmail(newUser);
            result.getSuccess().add(userDTO);
        } catch (Exception e) {
            result.getErrorOrCreate().add(new UserWithErrorDTO(userDTO, "errorWhileSendingEmail"));
        }
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
