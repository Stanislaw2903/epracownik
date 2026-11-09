package com.eurosoftit.ebok.common.ftp.impl;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.apache.commons.net.ftp.FTP;

@Getter
@Setter
@ConfigurationProperties(prefix = "ftp", ignoreUnknownFields = false)
public class FTPParams {
	private String host;
	private Integer port;
	private String login;
	private String password;
	private String dir;
	private byte[] keyStore;
	private String keyStoreType;
	private String keyPassword;
	int fileType = FTP.ASCII_FILE_TYPE;
	private FTPType ftpType;
	private boolean storeRetrieved;
	private String retrievedFileDir;
    private boolean zip;
    private FTPMethodStore ftpMethodStore = FTPMethodStore.NOT_UNIQ;

	public enum FTPType {
		FTP,FTPS,SFTP
	}

    public enum FTPMethodStore {
        UNIQ,NOT_UNIQ
    }
}
