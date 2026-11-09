package com.eurosoftit.ebok.common.ftp.impl;

import org.apache.commons.net.ftp.FTPFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FTPSDecorator implements FTP {

	private FTPSClient ftpClient;

	public FTPSDecorator(FTPSClient ftpClient) {
		this.ftpClient = ftpClient;
	}

	@Override
	public void disconnect() throws IOException {
		ftpClient.disconnect();
	}

	@Override
	public void connect(String host, Integer port) throws IOException {
		ftpClient.connect(host,port);
	}

	@Override
	public void connect(String host) throws IOException {
		ftpClient.connect(host);
	}

	@Override
	public boolean login(String userName, String password) throws IOException {
		return ftpClient.login(userName,password);
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
		try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			if(ftpClient.retrieveFile(fileName, outputStream)){
				return FileDto.builder().name(fileName).content(outputStream.toByteArray()).build();
			}
		}

		return null;
	}

	@Override
	public boolean deleteFile(String filePath) throws IOException {
		return ftpClient.deleteFile(filePath);
	}

	@Override
	public boolean storeUniqueFile(String fileName, InputStream inputStream) throws IOException {
		return ftpClient.storeUniqueFile(fileName,inputStream);
	}

	@Override
	public boolean changeWorkingDirectory(String dir) throws IOException {
        ftpClient.changeToParentDirectory();
        boolean isChanged = ftpClient.changeWorkingDirectory(dir);

        if (!isChanged) {
            ftpClient.makeDirectory(dir);
        }

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
