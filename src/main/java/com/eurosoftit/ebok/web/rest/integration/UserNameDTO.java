package com.eurosoftit.ebok.web.rest.integration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class UserNameDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;

    public UserNameDTO(Long id, String login, String first, String last) {
        this.id = id;
        List<String> list = Lists.newArrayList();
        list.add(login);
        list.add(first);
        list.add(last);
        this.name = StringUtils.join(list, " ");
    }

}
