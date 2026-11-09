package com.eurosoftit.ebok.common.ftp.impl;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.net.ftp.FTP;

class FTPUniqDecorator extends FTPDecorator {

    public FTPUniqDecorator(FTPClient ftpClient) {
        super(ftpClient);
    }

    @Override
    public boolean storeUniqueFile(String fileName, InputStream inputStream) throws IOException {
        return ftpClient.storeUniqueFile(fileName, inputStream);
    }

    @Override
    public void setFileType(int fileType) throws IOException {
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.enterLocalPassiveMode();
    }
}
