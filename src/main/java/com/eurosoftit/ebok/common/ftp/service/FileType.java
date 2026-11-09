package com.eurosoftit.ebok.common.ftp.service;

import org.apache.commons.lang3.StringUtils;

public enum FileType {
    PIT_11 {
        @Override
        String buildPrefix(Long year, String lastName, String firstName) {
            return "PIT11_" + year + "_" + StringUtils.trim(lastName) + "_" + StringUtils.trim(firstName);
        }
    }, IMIR {
        @Override
        String buildPrefix(Long year, String lastName, String firstName) {
            return "IMIR_" + year + "_" + StringUtils.trim(lastName) + "_" + StringUtils.trim(firstName);
        }
    };

    abstract String buildPrefix(Long year, String lastName, String firstName);
}
