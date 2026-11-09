package com.eurosoftit.ebok.common.ftp.impl;

import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.util.Objects;

public class StoreOnLANDecorator implements FTP {

    private FTP ftpClient;
    private String retrievedFileDir;

    StoreOnLANDecorator(FTP ftp, String retrievedFileDir) {
        this.ftpClient = ftp;
        this.retrievedFileDir = retrievedFileDir;
    }

    @Override
    public void disconnect() throws IOException {
        ftpClient.disconnect();
    }

    @Override
    public void connect(String host, Integer port) throws IOException {
        ftpClient.connect(host, port);
    }

    @Override
    public void connect(String host) throws IOException {
        ftpClient.connect(host);
    }

    @Override
    public boolean login(String userName, String password) throws IOException {
        return ftpClient.login(userName, password);
    }

    @Override
    public void enterLocalPassiveMode() {
        ftpClient.enterLocalPassiveMode();
    }

    @Override
    public FTPFile[] listFiles() throws IOException {
        return ftpClient.listFiles();
    }

    @Override
    public FTPFile[] listFiles(String dir) throws IOException {
        return ftpClient.listFiles(dir);
    }

    @Override
    public boolean isConnected() {
        return ftpClient.isConnected();
    }

    @Override
    public FTPFile[] listDirectories(String path) throws IOException {
        return ftpClient.listDirectories(path);
    }

    @Override
    public FileDto retrieveFile(String fileName) throws IOException {
        FileDto retrieved = ftpClient.retrieveFile(fileName);

            if (Objects.nonNull(retrieved)) {
                File file = new File(retrievedFileDir + "//" + retrieved.getName());
                try (OutputStream fileOut = new FileOutputStream(file)) {
                    fileOut.write(retrieved.getContent());
                }
            }

        return retrieved;
    }

    @Override
    public boolean deleteFile(String filePath) throws IOException {
        return ftpClient.deleteFile(filePath);
    }

    @Override
    public boolean storeUniqueFile(String fileName, InputStream inputStream) throws IOException {
        return ftpClient.storeUniqueFile(fileName, inputStream);
    }

    @Override
    public boolean changeWorkingDirectory(String dir) throws IOException {
        return ftpClient.changeWorkingDirectory(dir);
    }

    @Override
    public void setFileType(int fileType) throws IOException {
        ftpClient.setFileType(fileType);
    }

    @Override
    public int getReplyCode() {
        return ftpClient.getReplyCode();
    }
}
