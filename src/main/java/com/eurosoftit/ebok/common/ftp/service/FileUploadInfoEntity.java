package com.eurosoftit.ebok.common.ftp.service;

import com.eurosoftit.ebok.domain.AbstractEntity;
import com.eurosoftit.ebok.domain.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "file_upload_info")
@Setter
@Getter
public class FileUploadInfoEntity extends AbstractEntity {
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private User user;
    private String path;
    private String name;
    @Enumerated(EnumType.STRING)
    private FileType type;
    private LocalDateTime viewed;
    private LocalDateTime confirmed;
    private Long year;
}

