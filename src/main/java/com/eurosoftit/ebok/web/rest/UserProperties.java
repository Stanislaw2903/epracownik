package com.eurosoftit.ebok.web.rest;

import com.eurosoftit.ebok.domain.specification.Equal;
import com.eurosoftit.ebok.domain.specification.Like;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserProperties {
    @Like
    private String login;
    @Like
    private String firstName;
    @Like
    private String lastName;
    @Like
    private String email;
    @Like
    private String phoneNumber;
    @Equal(field = "group.id")
    private List<Integer> groups;
    @Equal(field = "activated")
    private Boolean activated;
    @Like
    private String type;
}
