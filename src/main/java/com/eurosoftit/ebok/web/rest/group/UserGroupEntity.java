package com.eurosoftit.ebok.web.rest.group;

import com.eurosoftit.ebok.common.controller.IEntity;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "USER_GROUP")
@Data
@SequenceGenerator(name = "USER_GROUP_SEQ", sequenceName = "USER_GROUP_SEQ", allocationSize = 1)
public class UserGroupEntity implements IEntity<Long>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_GROUP_SEQ")
    @Column(name = "id")
    private Long id;
    private String code;
    private String name;

    @Override
    public boolean isNew() {
        return id == null;
    }
}
