package com.eurosoftit.ebok.web.rest.persondata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.ZonedDateTime;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class CoursesDTO implements Serializable {
    private String name;
    private ZonedDateTime completionDate;
    private String donated;
}
