package com.eurosoftit.ebok.common.ftp.service;

import com.eurosoftit.ebok.common.ftp.impl.FTPException;
import com.eurosoftit.ebok.common.ftp.impl.FTPParams;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FTPClientConfiguration {

    @Bean
    FTPClient ftpClient(FTPParams ftpParams) throws FTPException {

        if (ftpParams.getHost() != null) {
            FTPClientImpl ftpClient = new FTPClientImpl();
            ftpClient.prepareConnection(ftpParams);
            ftpClient.disconnect();
            return ftpClient;
        }

        return new FTPLocalDiskImpl();
    }
}
