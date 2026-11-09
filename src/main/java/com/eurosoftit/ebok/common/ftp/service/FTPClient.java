package com.eurosoftit.ebok.common.ftp.service;

import com.eurosoftit.ebok.common.ftp.impl.FTPException;
import com.eurosoftit.ebok.common.ftp.impl.FTPParams;
import com.eurosoftit.ebok.common.ftp.impl.FileDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FTPClient {
    void prepareConnection(FTPParams ftpParams) throws FTPException;

    void login() throws FTPException;

    void disconnect() throws FTPException;

    List<String> getFileNames() throws FTPException;
    List<String> getFileNames(String dir) throws FTPException;

    boolean storeFile(MultipartFile file, String relateFileName, String dir) throws IOException, FTPException;

    boolean storeFiles(List<MultipartFile> files, String dir) throws FTPException, IOException;

    boolean isConnected();

    List<String> getDirectories(String path) throws FTPException;

    FileDto getFile(String fileName) throws FTPException;

    FileDto getFile(String fileDir, String fileName) throws FTPException;

    List<FileDto> getFiles(String fileDir) throws FTPException;

    boolean removeFile(String filePath) throws FTPException;

    boolean removeFile(String fileDir, String fileName) throws FTPException;

    void setFileType(int fileType);
}
