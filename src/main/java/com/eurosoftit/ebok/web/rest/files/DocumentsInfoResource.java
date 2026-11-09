package com.eurosoftit.ebok.web.rest.files;

import com.eurosoftit.ebok.common.ftp.service.FTPService;
import com.eurosoftit.ebok.common.ftp.service.FileType;
import com.eurosoftit.ebok.domain.DictionaryDTO;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Api
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/documents")
public class DocumentsInfoResource {

    private final FTPService ftpService;

    @GetMapping
    public Page<FileInfoDTO> get(FileInfoParams params, Pageable pageable) {
        return ftpService.find(params, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), getCustomSort(pageable)));
    }

    public Sort getCustomSort(Pageable pageable) {
        List<Sort.Order> orders = new ArrayList<>();
        Sort sort = pageable.getSort();
        if (sort == null) {
            return null;
        }

        for (Sort.Order order : sort) {
            if ("userId".equals(order.getProperty())) {
                orders.add(new Sort.Order(order.getDirection(), "user.externalId"));
            } else {
                orders.add(order);
            }
        }
        return Sort.by(orders);
    }

    @GetMapping("/types")
    public List<DictionaryDTO> getFiles() {
        return Arrays.stream(FileType.values()).map(this::toDto).collect(Collectors.toList());
    }

    private DictionaryDTO toDto(FileType fileType) {
        return new DictionaryDTO(Long.valueOf(fileType.ordinal()), fileType.name());
    }
}
