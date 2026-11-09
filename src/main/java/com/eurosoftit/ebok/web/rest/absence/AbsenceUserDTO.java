package com.eurosoftit.ebok.web.rest.absence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbsenceUserDTO implements Serializable {
    private Long id;
    private Long externalId;
    private String firstName;
    private String lastName;
}
