package com.eurosoftit.ebok.web.rest.group;

import com.eurosoftit.ebok.domain.specification.Equal;
import com.eurosoftit.ebok.domain.specification.Like;
import lombok.Data;

import java.util.List;

@Data
public class UserGroupParams {
    @Equal(field = "id")
    private List<Long> ids;
    @Like
    private String name;
    @Like(field = "code")
    private String code;
}
