package com.eurosoftit.ebok.web.rest.integration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserWithErrorDTO {
    private UserDTO user;
    private String error;
}
