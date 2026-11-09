package com.eurosoftit.ebok.web.rest.payroll.dto;

import com.eurosoftit.ebok.common.ftp.impl.FileDto;
import com.eurosoftit.ebok.common.ftp.service.FileType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayrollFileDTO implements Serializable {
    private FileType fileType;
    private Long id;
    private LocalDateTime viewed;
    private LocalDateTime confirmed;
    private FileDto file;
}
