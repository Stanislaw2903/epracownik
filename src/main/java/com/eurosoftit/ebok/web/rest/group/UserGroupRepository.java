package com.eurosoftit.ebok.web.rest.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserGroupRepository extends JpaRepository<UserGroupEntity, Long>, JpaSpecificationExecutor<UserGroupEntity> {
}
