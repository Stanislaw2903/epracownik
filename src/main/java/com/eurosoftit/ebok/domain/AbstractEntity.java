package com.eurosoftit.ebok.domain;

import com.eurosoftit.ebok.common.controller.IEntity;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@MappedSuperclass
public abstract class AbstractEntity implements Serializable, IEntity<Long> {

    private static final long serialVersionUID = 4113738194351517815L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Override
    public boolean isNew() {
        return id == null;
    }
}
