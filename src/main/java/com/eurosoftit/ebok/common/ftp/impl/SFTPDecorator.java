package com.eurosoftit.ebok.common.ftp.impl;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Objects;
import java.util.Properties;
import java.util.Vector;

@Slf4j
class SFTPDecorator implements FTP {

	public static final String SFTP = "sftp";

	private SFTPClient sftpClient;
	private Session session = null;
	private ChannelSftp channel = null;

	private int port;
	private String host;

	public SFTPDecorator(SFTPClient sftpClient) {
		this.sftpClient = sftpClient;
	}

	@Override
	public void disconnect() {
		if (channel != null) {
			log.debug("Disconnecting channel");
			channel.disconnect();
		}
		if (session != null) {
			log.debug("Disconnecting session");
			session.disconnect();
		}
	}

	@Override
	public void connect(String host, Integer port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public void connect(String host){
	this.host =host;
	}

	@Override
	public boolean login(String userName, String password) throws IOException {

		try {
			log.debug("Initializing jsch");
			openSession(userName, password);
			log.info("Connected !");
			log.debug("Opening a channel ...");
			openChannel();
			log.debug("Channel sftp opened");
			return true;
		} catch (JSchException e) {
			throw new IOException(e);
		}
	}

	private void openChannel() throws JSchException {
		channel = (ChannelSftp) session.openChannel(SFTP);
		channel.connect();
	}

	private void openSession(String userName, String password) throws JSchException {
		session = Objects.nonNull(port) ? sftpClient.getSession(userName, host, port) : sftpClient.getSession(userName, host);
		if(StringUtils.isNotBlank(password)) {
			session.setPassword(password.getBytes(Charset.forName("ISO-8859-1")));
		}
		log.debug("Jsch set to StrictHostKeyChecking=no");
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);

		if(Objects.nonNull(port)){
			log.info("Connecting to " + host + ":" + port);
		}else {
			log.info("Connecting to " + host);
		}
		session.connect();
	}

	@Override
	public void enterLocalPassiveMode() {
	}

	@Override
	public FTPFile[] listFiles() throws IOException {
		try {
			return toFTPFiles(channel.ls("*"), false);
		} catch (SftpException e) {
			throw new IOException(e);
		}
	}

	@Override
	public FTPFile[] listFiles(String dir) throws IOException {
		try {
			return toFTPFiles(channel.ls(dir), false);
		} catch (SftpException e) {
			throw new IOException(e);
		}
	}

	private FTPFile[] toFTPFiles(Vector fileList, boolean dir) {

		FTPFile[] ftpFiles =  new FTPFile[fileList.size()];
		for(int i=0; i<fileList.size();i++){
			ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) fileList.get(i);
			FTPFile ftpFile = new FTPFile();
			ftpFile.setName(entry.getFilename());

			SftpATTRS attrs = entry.getAttrs();

			if((!dir && attrs.isDir())||(dir && !attrs.isDir())){
				continue;
			}

			ftpFile.setType(attrs.isDir() ? FTPFile.DIRECTORY_TYPE : FTPFile.FILE_TYPE);

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(attrs.getMTime());
			ftpFile.setTimestamp(calendar);

			ftpFile.setSize(attrs.isDir() ? 0 : attrs.getSize());
			setPermission(ftpFile, attrs.getPermissions());
			ftpFiles[i] = ftpFile;
		}

		return ftpFiles;
	}

	@Override
	public boolean isConnected() {
		return Objects.nonNull(session) && session.isConnected();
	}

	@Override
	public FTPFile[] listDirectories(String path) throws IOException {
		try {
			return toFTPFiles(channel.ls(path), true);
		} catch (SftpException e) {
			throw new IOException(e);
		}
	}

	@Override
	public FileDto retrieveFile(String fileName) throws IOException {
		if (channel == null || session == null || !session.isConnected() || !channel.isConnected()) {
			throw new IOException("Connection to server is closed. Open it first.");
		}

		try(ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream()) {
			log.debug("Downloading file to server");
			channel.get(fileName, arrayOutputStream);
			log.info("Download successfull");

			return FileDto.builder().name(fileName).content(arrayOutputStream.toByteArray()).build();
		} catch (SftpException e) {
			log.debug("Download failure",e.getMessage());
		}

		return null;
	}

	@Override
	public boolean deleteFile(String filePath) throws IOException {
		try {
			channel.rm(filePath);
			return true;
		} catch (SftpException e) {
			throw new IOException(e);
		}
	}

	@Override
	public boolean storeUniqueFile(String fileName, InputStream inputStream) throws IOException {
		try {
			channel.put(inputStream, fileName);
			return true;
		} catch (SftpException e) {
			throw new IOException(e);
		}
	}

	@Override
	public boolean changeWorkingDirectory(String dir) throws IOException {
		try {
			channel.cd(dir);
			return true;
		} catch (SftpException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void setFileType(int fileType) {
	}

	@Override
	public int getReplyCode() {
		return 200;
	}

	private void setPermission(FTPFile file, int mode) {

		file.setPermission(FTPFile.WORLD_ACCESS, FTPFile.EXECUTE_PERMISSION, (1&mode)>0);
		file.setPermission(FTPFile.WORLD_ACCESS, FTPFile.WRITE_PERMISSION, (2&mode)>0);
		file.setPermission(FTPFile.WORLD_ACCESS, FTPFile.READ_PERMISSION, (4&mode)>0);

		file.setPermission(FTPFile.GROUP_ACCESS, FTPFile.EXECUTE_PERMISSION, (8&mode)>0);
		file.setPermission(FTPFile.GROUP_ACCESS, FTPFile.WRITE_PERMISSION, (16&mode)>0);
		file.setPermission(FTPFile.GROUP_ACCESS, FTPFile.READ_PERMISSION, (32&mode)>0);

		file.setPermission(FTPFile.USER_ACCESS, FTPFile.EXECUTE_PERMISSION, (64&mode)>0);
		file.setPermission(FTPFile.USER_ACCESS, FTPFile.WRITE_PERMISSION, (128&mode)>0);
		file.setPermission(FTPFile.USER_ACCESS, FTPFile.READ_PERMISSION, (256&mode)>0);
	}

}
