package com.eurosoftit.ebok.web.rest.files;

import com.eurosoftit.ebok.common.ftp.service.FileType;
import com.eurosoftit.ebok.domain.specification.Equal;
import com.eurosoftit.ebok.domain.specification.Like;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class FileInfoParams {
    @Equal(field = "user.id")
    private List<Long> userIds;
    @Equal(field = "type")
    private FileType fileType;
    @Equal
    private Long year;
    private Boolean onlyConfirmed;
}
