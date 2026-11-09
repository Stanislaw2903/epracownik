package com.eurosoftit.ebok.common.ftp.service;

import com.eurosoftit.ebok.common.ftp.impl.FileDto;
import com.eurosoftit.ebok.domain.User;
import com.eurosoftit.ebok.domain.specification.SpecificationReflectionBuilder;
import com.eurosoftit.ebok.web.rest.UserUtils;
import com.eurosoftit.ebok.web.rest.files.FileInfoDTO;
import com.eurosoftit.ebok.web.rest.files.FileInfoParams;
import com.eurosoftit.ebok.web.rest.payroll.dto.PayrollFileDTO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.JoinType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FTPService {
    private final FTPClient ftpClient;
    private final FTPRepository ftpRepository;
    private final UserUtils userUtils;

    @SneakyThrows
    @Transactional
    public boolean setViewed(Long id) {
        if (ftpRepository.existsById(id)) {
            return true;
        }

        ftpRepository.setViewed(id, LocalDateTime.now());
        return true;
    }

    @SneakyThrows
    @Transactional
    public boolean setConfirmed(Long id) {
        if (ftpRepository.existsById(id) && ftpRepository.isConfirmed(id)) {
            return true;
        }

        ftpRepository.setConfirmed(id, LocalDateTime.now());

        return true;
    }

    @SneakyThrows
    @Transactional
    public boolean exists(String dir, String name, Long id) {
        return ftpRepository.existsByName(dir, name, id);
    }

    @SneakyThrows
    public FileUploadInfoEntity getFile(String dir, String name, Long id) {
        return ftpRepository.findByName(dir, name, id);
    }

    @SneakyThrows
    @Transactional
    public boolean findExists(FileType fileType, Long year, String dir) {
        final User user = userUtils.getLoggerUserReference();
        final String filename = removeSpaces(fileType.buildPrefix(year, user.getLastName(), user.getFirstName()));
        final List<String> fileNames = ftpClient.getFileNames(dir);
        if (fileNames != null) {
            return fileNames.stream().anyMatch(file -> removeSpaces(file).toLowerCase().startsWith(filename.toLowerCase()));
        }

        return false;
    }

    @SneakyThrows
    @Transactional
    public FileDto find(String dir, String prefix) {
        List<FileDto> fileNames = ftpClient.getFiles(dir);
        if (fileNames != null) {
            return fileNames.stream().filter(file -> removeSpaces(file.getName()).toLowerCase().startsWith(prefix.toLowerCase())).findFirst().orElse(null);
        }

        return null;
    }

    private String removeSpaces(String name) {
        return name.replaceAll(" ", "");
    }

    @Transactional
    public FileUploadInfoEntity save(FileUploadInfoEntity fileUploadInfo) {
        return ftpRepository.save(fileUploadInfo);
    }

    @Transactional
    public PayrollFileDTO getFile(FileType fileType, Long year, String dir) {
        final User user = userUtils.getLoggerUserReference();
        final String filename = removeSpaces(fileType.buildPrefix(year, user.getLastName(), user.getFirstName()));
        final FileDto fileDto = find(dir, filename);

        if (exists(dir, fileDto.getName(), user.getId())) {
            final FileUploadInfoEntity file = getFile(dir, fileDto.getName(), user.getId());
            return new PayrollFileDTO(fileType, file.getId(), file.getViewed(), file.getConfirmed(), fileDto);
        } else {
            final FileUploadInfoEntity payrollFile = new FileUploadInfoEntity();
            payrollFile.setPath(dir);
            payrollFile.setName(fileDto.getName());
            payrollFile.setUserId(user.getId());
            payrollFile.setViewed(LocalDateTime.now());
            payrollFile.setYear(year);
            payrollFile.setType(fileType);
            final FileUploadInfoEntity file = save(payrollFile);

            return new PayrollFileDTO(fileType, file.getId(), file.getViewed(), file.getConfirmed(), fileDto);
        }
    }

    public Page<FileInfoDTO> find(FileInfoParams params, Pageable pageable) {
        Specification<FileUploadInfoEntity> process = SpecificationReflectionBuilder.process(params);

        if (params.getOnlyConfirmed() != null) {
            process = process.and(onlyconfirmed(params.getOnlyConfirmed()));
        }

        return ftpRepository.findAll(process, pageable).map(this::map);
    }

    private Specification<FileUploadInfoEntity> onlyconfirmed(Boolean value) {
        return (eventRoot, criteriaQuery, criteriaBuilder) ->
            Objects.isNull(value) ? null : value ? criteriaBuilder.isNotNull(eventRoot.get("confirmed")) : criteriaBuilder.isNull(eventRoot.get("confirmed"));
    }

    private FileInfoDTO map(FileUploadInfoEntity fileUploadInfoEntity) {
        final User user = fileUploadInfoEntity.getUser();
        final FileInfoDTO file = new FileInfoDTO();
        file.setConfirmed(fileUploadInfoEntity.getConfirmed());
        file.setViewed(fileUploadInfoEntity.getViewed());
        file.setUserId(user.getExternalId());
        file.setLastName(user.getLastName());
        file.setFirstName(user.getFirstName());
        file.setFileType(fileUploadInfoEntity.getType());
        file.setFileName(fileUploadInfoEntity.getName());
        return file;
    }
}
