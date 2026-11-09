package com.eurosoftit.ebok.web.rest.integration;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SimpleUserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String login;

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLogin() {
        return login;
    }
}
