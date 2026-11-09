package com.eurosoftit.ebok.web.rest.absence;

import lombok.Data;

import java.io.Serializable;

@Data
public class AbsenceId implements Serializable {
    private Long id;
    private Long ospId;
}
