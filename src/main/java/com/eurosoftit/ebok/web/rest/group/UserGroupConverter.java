package com.eurosoftit.ebok.web.rest.group;

import com.eurosoftit.ebok.common.controller.ContentConverter;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserGroupConverter implements ContentConverter<UserGroupEntity, Long, UserGroupDTO> {
    @Override
    public UserGroupEntity convert(UserGroupDTO userGroupDTO) {
        UserGroupEntity entity = new UserGroupEntity();
        entity.setCode(userGroupDTO.getCode());
        entity.setId(userGroupDTO.getId());
        entity.setName(userGroupDTO.getName());
        return entity;
    }

    @Override
    public UserGroupDTO convert(UserGroupEntity entity) {
        UserGroupDTO dto = new UserGroupDTO();
        dto.setCode(entity.getCode());
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }

    @Override
    public List<UserGroupDTO> convert(List<UserGroupEntity> entityList) {
        return entityList.stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    public Page<UserGroupDTO> convert(Page<UserGroupEntity> entityList) {
        return entityList.map(this::convert);
    }
}
