package com.eurosoftit.ebok.common.ftp.impl;

import com.jcraft.jsch.JSch;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

@Getter
@Setter
class SFTPClient extends JSch {
    public SFTPClient(byte[] keyStore, String keyPassword) {
        super();
        addCert(keyStore, keyPassword);
    }

    @SneakyThrows
    private void addCert(byte[] keyStore, String keyPassword) {
        if(ArrayUtils.isNotEmpty(keyStore)){
            addIdentity("", keyStore, null, StringUtils.getBytesUtf8(keyPassword));
        }
    }

}
