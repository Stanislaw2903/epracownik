package com.eurosoftit.ebok.common.ftp.service;

import com.eurosoftit.ebok.common.ftp.impl.*;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class FTPClientImpl implements FTPClient {

    @Getter
    private FTP ftp;

    private FTPParams ftpParams;

    public void prepareConnection(FTPParams ftpParams) throws FTPException {
        this.ftpParams = ftpParams;
        chooseFTPClientImplementation(ftpParams);
        initFTP(ftpParams.getHost(), ftpParams.getPort());
        login(ftpParams.getLogin(), ftpParams.getPassword());
        setFileType(ftpParams.getFileType());
    }

    @Override
    public void disconnect() throws FTPException {
        try {
            ftp.disconnect();
        } catch (IOException e) {
            throw new FTPException("Unable to disconnect to FTP", e);
        }
    }

    private void chooseFTPClientImplementation(FTPParams ftpParams) {
        ftp = FTPFactory.getInstance(ftpParams);
    }

    private void initFTP(String host, Integer port) throws FTPException {
        try {
            log.info("Trying to init ftp server");
            connect(host, port);

            if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                ftp.disconnect();
                throw new FTPException("Couldn't connect to FTP server.");
            }
            log.info("FTP server inited successful");
        } catch (Exception e) {
            log.error("Unable to connect to FTP", e.getMessage());
            throw new FTPException("Unable to connect to FTP", e);
        }
    }

    @SneakyThrows
    private void connect(String host, Integer port) {
        if (Objects.nonNull(port)) {
            ftp.connect(host, port);
        } else {
            ftp.connect(host);
        }
    }

    private void login(String userName, String password) throws FTPException {
        try {
            log.info("Trying to log in the user: {}", userName);
            ftp.enterLocalPassiveMode();
            boolean login = ftp.login(userName, password);
            if (login) {
                log.info("User logged to FTP server.");
            } else {
                throw new FTPException("Incorrect password for FTP user.");
            }
        } catch (Exception e) {
            log.error("Unable to login to FTP", e.getMessage());
            throw new FTPException("Unable to login to FTP", e);
        }
    }

    public void login() throws FTPException {
        login(ftpParams.getLogin(), ftpParams.getPassword());
    }

    @Override
    public List<String> getFileNames() throws FTPException {
        try {

            reinit();

            if (isConnected()) {
                log.trace("Gathering files names");

                FTPFile[] ftpFiles = ftp.listFiles();

                if (ArrayUtils.isEmpty(ftpFiles)) {
                    return Collections.emptyList();
                }

                return Arrays.stream(ftpFiles).filter(FTPFile::isFile).map(FTPFile::getName).collect(Collectors.toList());
            }
            throw new FTPException("FTP client not connected");
        } catch (IOException e) {
            log.error("Error while getting files names");
            throw new FTPException("Error while getting files names", e);
        } finally {
            disconnect();
        }
    }

    @Override
    public List<String> getFileNames(String dir) throws FTPException {
        try {

            reinit();

            if (isConnected()) {
                log.trace("Gathering files names");
                changeDir(dir);
                FTPFile[] ftpFiles = ftp.listFiles();

                if (ArrayUtils.isEmpty(ftpFiles)) {
                    return Collections.emptyList();
                }

                return Arrays.stream(ftpFiles).filter(FTPFile::isFile).map(FTPFile::getName).collect(Collectors.toList());
            }
            throw new FTPException("FTP client not connected");
        } catch (IOException e) {
            log.error("Error while getting files names");
            throw new FTPException("Error while getting files names", e);
        } finally {
            disconnect();
        }
    }

    private void reinit() throws FTPException {
        if (!isConnected()) {
            connect(ftpParams.getHost(), ftpParams.getPort());
            login(ftpParams.getLogin(), ftpParams.getPassword());
            setFileType(ftpParams.getFileType());
        }
    }

    @Override
    public boolean isConnected() {
        boolean connected = Objects.nonNull(ftp) && ftp.isConnected();
        if (!connected) {
            log.info("Ftp server not connected.");
        }
        return connected;
    }

    @Override
    public List<String> getDirectories(String path) throws FTPException {
        try {
            reinit();

            if (isConnected()) {
                log.trace("Gathering directories");
                FTPFile[] ftpFiles = ftp.listDirectories(path);
                return Arrays.stream(ftpFiles).filter(FTPFile::isDirectory).map(FTPFile::getName).collect(Collectors.toList());
            }
            throw new FTPException("FTP client not connected");
        } catch (IOException e) {
            log.error("Error while getting directories: {}", path);
            throw new FTPException("Error while getting directories", e);
        } finally {
            disconnect();
        }
    }

    @Override
    public FileDto getFile(String fileName) throws FTPException {
        return getFile(StringUtils.EMPTY, fileName);
    }

    @Override
    public FileDto getFile(String fileDir, String fileName) throws FTPException {
        try {
            reinit();

            if (isConnected()) {
                try {
                    changeDir(fileDir);
                    log.trace("Retrieving file: {}", fileName);
                    return ftp.retrieveFile(fileName);
                } catch (IOException e) {
                    log.error("Error while getting file: {}", fileName);
                    throw new FTPException("Error while getting file", e);
                }
            } else {
                throw new FTPException("ftp client not connected");
            }
        } catch (FTPException e) {
            throw e;
        } finally {
            disconnect();
        }
    }

    @Override
    public List<FileDto> getFiles(String fileDir) throws FTPException {
        try {
            reinit();

            if (isConnected()) {
                log.trace("Gathering files from dir: {}", fileDir);
                changeDir(fileDir);
                List<FileDto> ftpFiles = new ArrayList<>();
                FTPFile[] files = ftp.listFiles(fileDir);

                if (ArrayUtils.isNotEmpty(files)) {
                    for (FTPFile ftpFile : files) {
                        if (ftpFile.isFile()) {
                            ftpFiles.add(getFile(ftpFile.getName()));
                        }
                    }
                }

                return ftpFiles;
            }
            throw new FTPException("ftp client not connected");
        } catch (IOException e) {
            log.error("Error while getting files from dir: {}", fileDir);
            throw new FTPException("Error while getting files from dir", e);
        } finally {
            disconnect();
        }
    }

    private byte[] getFileContent(ByteArrayOutputStream outputStream) throws IOException {
        if (Objects.isNull(outputStream)) {
            return null;
        }

        return outputStream.toByteArray();
    }

    @Override
    public boolean removeFile(String filePath) throws FTPException {
        try {
            reinit();

            if (isConnected()) {
                log.trace("Deleting file: {}", filePath);
                return ftp.deleteFile(filePath);
            }
            throw new FTPException("FTP client not connected");
        } catch (IOException e) {
            log.error("Failed deleting file: {}", filePath);
            throw new FTPException("Failed deleting file");
        } finally {
            disconnect();
        }
    }

    @Override
    public boolean removeFile(String fileDir, String fileName) throws FTPException {
        return removeFile(fileDir + "/" + fileName);
    }

    public boolean storeFile(MultipartFile file, String relateFileName, String dir) throws IOException, FTPException {
        InputStream inputStream = null;
        try {

            reinit();

            if (isConnected()) {
                changeDir(dir);
                log.trace("Adding new file. Name: {}", relateFileName);
                inputStream = file.getInputStream();

                return ftp.storeUniqueFile(relateFileName, inputStream);
            }
        } finally {
            if (Objects.nonNull(inputStream)) {
                inputStream.close();
            }
            disconnect();
        }

        throw new FTPException("FTP client not connected");
    }

    public boolean storeFiles(List<MultipartFile> files, String dir) throws FTPException, IOException {

        try {
            reinit();
            if (isConnected()) {
                if (files != null) {
                    changeDir(dir);
                    List<String> newFilesName = new ArrayList<>();
                    try {
                        for (MultipartFile file : files) {
                            log.trace("Adding new file. Name: {}", file.getName());
                            addFileToServer(file);
                            newFilesName.add(file.getName());
                        }
                        return true;
                    } catch (Exception e) {
                        deleteAddedFiles(newFilesName);
                        throw new FTPException("Error while adding new files");
                    }
                }
            }
        } finally {
            disconnect();
        }
        throw new FTPException("FTP client not connected");
    }

    private void addFileToServer(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        ftp.storeUniqueFile(file.getName(), inputStream);
    }

    private void changeDir(String dir) throws IOException, FTPException {

        if (StringUtils.isNotEmpty(dir)) {
            boolean changedDirectory = ftp.changeWorkingDirectory(dir);
            if (!changedDirectory) {
                log.error("Failed to change directory: {}", dir);
                throw new FTPException("Unable to change directory: " + dir);
            }
        }
        log.trace("Successfully changed directory: {}", dir);
    }

    private void deleteAddedFiles(List<String> newFilesName) {
        for (String addedFileName : newFilesName) {
            try {
                log.trace("Deleting added file: {}", addedFileName);
                ftp.deleteFile(addedFileName);
            } catch (IOException e1) {
                log.error("Failed to delete added file.");
            }
        }
    }

    @Override
    public void setFileType(int fileType) {
        try {
            ftp.setFileType(fileType);
        } catch (IOException ex) {
            log.error("Error while setting up file type", ex);
        }

    }
}
