package com.eurosoftit.ebok.common.ftp.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class FileUploadInfoDTO {
    private Long id;
    private Long taskId;
    private String name;
}
