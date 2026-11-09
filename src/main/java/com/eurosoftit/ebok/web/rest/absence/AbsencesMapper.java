package com.eurosoftit.ebok.web.rest.absence;

import com.eurosoftit.ebok.domain.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AbsencesMapper {
    AbsenceUserDTO map(User user);
}
