package com.eurosoftit.ebok.web.rest.management;

import com.eurosoftit.ebok.domain.AbstractEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "MANAGEMENT")
@Data
public class ManagementEntity extends AbstractEntity {
    @Enumerated(EnumType.STRING)
    private ManagementType type;
    private String value;
}
