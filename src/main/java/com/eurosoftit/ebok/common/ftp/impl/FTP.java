package com.eurosoftit.ebok.common.ftp.impl;

import org.apache.commons.net.ftp.FTPFile;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public interface FTP extends Closeable {
	void disconnect() throws IOException;

	void connect(String host, Integer port) throws IOException;

	void connect(String host) throws IOException;

	boolean login(String userName, String password) throws IOException;

	void enterLocalPassiveMode();

	FTPFile[] listFiles() throws IOException;

	FTPFile[] listFiles(String dir) throws IOException;

	boolean isConnected();

	FTPFile[] listDirectories(String path) throws IOException;

	FileDto retrieveFile(String fileName) throws IOException;

	boolean deleteFile(String filePath) throws IOException;

	boolean storeUniqueFile(String fileName, InputStream inputStream) throws IOException;

	boolean changeWorkingDirectory(String dir) throws IOException;

	void setFileType(int fileType) throws IOException;

	int getReplyCode();

	@Override
	default void close() throws IOException {
		disconnect();
	}
}
