package com.eurosoftit.ebok.common.ftp.service;

import com.eurosoftit.ebok.common.ftp.impl.FTPParams;
import com.eurosoftit.ebok.common.ftp.impl.FileDto;
import com.google.common.io.Files;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class FTPLocalDiskImpl implements FTPClient {

    @Override
    public void prepareConnection(FTPParams ftpParams) {
    }

    @Override
    public void login() {
    }

    @Override
    public void disconnect() {
    }

    @Override
    public List<String> getFileNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getFileNames(String dir) {
        return Stream.of(new File(dir).listFiles())
            .filter(file -> !file.isDirectory())
            .map(File::getName)
            .collect(Collectors.toList());
    }

    @Override
    public boolean storeFile(MultipartFile file, String relateFileName, String dir) throws IOException {
        File convFile = new File(relateFileName);
        FileUtils.writeByteArrayToFile(convFile, file.getBytes());
        return true;
    }

    @Override
    public boolean storeFiles(List<MultipartFile> files, String dir) throws IOException {
        for (MultipartFile file : files) {
            File convFile = new File(file.getName());
            FileUtils.writeByteArrayToFile(convFile, file.getBytes());
        }
        return true;
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public List<String> getDirectories(String path) {
        return Stream.of(new File(path).listFiles())
            .filter(file -> file.isDirectory())
            .map(File::getName)
            .collect(Collectors.toList());
    }

    @Override
    public FileDto getFile(String fileName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileDto getFile(String fileDir, String fileName) {
        File file = new File(fileDir + fileName);
        if (file == null) {
            return null;
        }

        try {
            return getFileDto(file);
        } catch (Exception e) {
            return null;
        }
    }

    @SneakyThrows
    private FileDto getFileDto(File file) {
        byte[] filesBytes = Files.toByteArray(file);
        return FileDto.builder().content(filesBytes).name(file.getName()).build();
    }

    @Override
    public List<FileDto> getFiles(String fileDir) {
        return Stream.of(new File(fileDir).listFiles())
            .filter(file -> !file.isDirectory())
            .map(this::getFileDto)
            .collect(Collectors.toList());
    }

    @Override
    public boolean removeFile(String filePath) {
        final File file = new File(filePath);
        if (file.delete()) {
            log.debug("Deleted the file: " + file.getName());
            return true;
        } else {
            log.debug("Failed to delete the file.");
            return false;
        }
    }

    @Override
    public boolean removeFile(String fileDir, String fileName) {
        final File file = new File(fileDir + fileName);
        if (file.delete()) {
            log.debug("Deleted the file: " + file.getName());
            return true;
        } else {
            log.debug("Failed to delete the file.");
            return false;
        }
    }

    @Override
    public void setFileType(int fileType) {
    }
}
