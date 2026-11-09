package com.eurosoftit.ebok.web.rest.group;

import com.eurosoftit.ebok.common.controller.CRUDResource;
import com.eurosoftit.ebok.common.controller.SearchService;
import com.eurosoftit.ebok.common.controller.Service;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/userGroup")
@RequiredArgsConstructor
public class UserGroupResource implements CRUDResource<Long, UserGroupEntity, UserGroupDTO, UserGroupParams>{

    @Getter
    private final UserGroupService searchService;
    @Getter
    private final UserGroupRepository repository;
    @Getter
    private final UserGroupConverter converter;

    @Override
    public Service<Long, UserGroupEntity, UserGroupDTO> getService() {
        return searchService;
    }
}
