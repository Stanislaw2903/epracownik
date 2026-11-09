package com.eurosoftit.ebok.common.ftp.impl;

import com.eurosoftit.ebok.common.ZipUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
class ZipFTPDecorator implements FTP {
	private static final String ZIP = ".zip";

	private FTP ftp;

	ZipFTPDecorator(FTP ftp) {
		this.ftp = ftp;
	}

	@Override
	public void disconnect() throws IOException {
		ftp.disconnect();
	}

	@Override
	public void connect(String host, Integer port) throws IOException {
		ftp.connect(host, port);
	}

	@Override
	public void connect(String host) throws IOException {
		ftp.connect(host);
	}

	@Override
	public boolean login(String userName, String password) throws IOException {
		return ftp.login(userName, password);
	}

	@Override
	public void enterLocalPassiveMode() {
		ftp.enterLocalPassiveMode();
	}

	@Override
	public FTPFile[] listFiles() throws IOException {
		return ftp.listFiles();
	}

	@Override
	public FTPFile[] listFiles(String dir) throws IOException {
		return ftp.listFiles(dir);
	}

	@Override
	public boolean isConnected() {
		return ftp.isConnected();
	}

	@Override
	public FTPFile[] listDirectories(String path) throws IOException {
		return ftp.listDirectories(path);
	}

	@Override
	public FileDto retrieveFile(String fileName) throws IOException {
		FileDto retrieved = getUnformattedFile(ftp.retrieveFile(fileName));
		return retrieved;
	}

	@Override
	public boolean deleteFile(String filePath) throws IOException {
		return ftp.deleteFile(filePath);
	}

	@Override
	public boolean storeUniqueFile(String fileName, InputStream inputStream) throws IOException {
		return ftp.storeUniqueFile(fileName, inputStream);
	}

	@Override
	public boolean changeWorkingDirectory(String dir) throws IOException {
		return ftp.changeWorkingDirectory(dir);
	}

	@Override
	public void setFileType(int fileType) throws IOException {
		ftp.setFileType(fileType);
	}

	@Override
	public int getReplyCode() {
		return ftp.getReplyCode();
	}

	private FileDto getUnformattedFile(FileDto fileDto) {

		if(Objects.isNull(fileDto)) {
			return null;
		}

		String fileName = fileDto.getName();
		byte[] content = fileDto.getContent();
		if (fileName.endsWith(ZIP)) {
			try {
				return FileDto.builder().name(fileName.replace(ZIP, StringUtils.EMPTY)).content(ZipUtil.unzip(content)).build();
			} catch (IOException e) {
				log.error("failed to unzip");
			}
		}

		return fileDto;
	}
}
