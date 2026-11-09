package com.eurosoftit.ebok.web.rest.payroll;

import com.eurosoftit.ebok.common.ftp.impl.FTPParams;
import com.eurosoftit.ebok.common.ftp.service.FTPService;
import com.eurosoftit.ebok.common.ftp.service.FileType;
import com.eurosoftit.ebok.web.rest.payroll.dto.PayrollFileDTO;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@Api
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payroll/documents")
public class DocumentsPayrollResource {

    private final FTPService ftpService;
    private final FTPParams ftpParams;

    @Transactional
    @GetMapping("/{fileType}/exists/{year}")
    Boolean pit11Exists(@PathVariable("fileType") FileType fileType, @PathVariable("year") Long year) {
        return ftpService.findExists(fileType, year, ftpParams.getDir());
    }

    @Transactional
    @GetMapping("/{fileType}/{year}")
    PayrollFileDTO getFile(@PathVariable("fileType") FileType fileType, @PathVariable("year") Long year) {
        return ftpService.getFile(fileType, year, ftpParams.getDir());
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> approve(@PathVariable("id") Long id) {
        ftpService.setConfirmed(id);
        return ResponseEntity.ok().build();
    }
}
