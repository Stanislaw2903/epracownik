package com.eurosoftit.ebok.web.rest.group;

import com.eurosoftit.ebok.common.controller.SearchService;
import com.eurosoftit.ebok.service.AbstractSearchService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserGroupService extends AbstractSearchService<UserGroupEntity, Long, UserGroupParams> implements com.eurosoftit.ebok.common.controller.Service<Long, UserGroupEntity, UserGroupDTO>, SearchService<Long, UserGroupEntity, UserGroupParams> {

    @Getter
    private final UserGroupRepository searchRepository;
    @Getter
    private final UserGroupConverter converter;

    @Override
    public JpaRepository<UserGroupEntity, Long> getRepository() {
        return searchRepository;
    }
}

