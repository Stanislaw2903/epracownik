package com.eurosoftit.ebok.web.rest.integration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IntegrationCandidateDTO {
    private Long externalId;
    private String firstName;
    private String lastName;
    private String email;
    private String type;
    private String problem;
}
