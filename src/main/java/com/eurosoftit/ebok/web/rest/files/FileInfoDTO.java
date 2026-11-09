package com.eurosoftit.ebok.web.rest.files;

import com.eurosoftit.ebok.common.ftp.service.FileType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileInfoDTO implements Serializable {
    private FileType fileType;
    private LocalDateTime viewed;
    private LocalDateTime confirmed;
    private String firstName;
    private String lastName;
    private Long userId;
    private String fileName;
}
