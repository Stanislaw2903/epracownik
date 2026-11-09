package com.eurosoftit.ebok.web.rest.group;

import com.eurosoftit.ebok.common.controller.DTO;
import lombok.Data;

@Data
public class UserGroupDTO implements DTO<Long> {
    private Long id;
    private String code;
    private String name;
}
