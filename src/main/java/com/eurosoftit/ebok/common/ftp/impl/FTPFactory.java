package com.eurosoftit.ebok.common.ftp.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.util.KeyManagerUtils;
import org.apache.commons.net.util.TrustManagerUtils;

import javax.net.ssl.KeyManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Objects;

@Slf4j
public class FTPFactory {

    @SneakyThrows
    public static FTP getInstance(FTPParams ftpParams) {
        FTP ftp = getFtp(ftpParams);

        if (ftpParams.isZip()) {
            ftp = new ZipFTPDecorator(ftp);
        }

        if (ftpParams.isStoreRetrieved()) {
            StoreType storeType = StoreType.LAN;
            switch (storeType) {
                case LAN:
                    ftp = new StoreOnLANDecorator(ftp, ftpParams.getRetrievedFileDir());
                    break;
                default:
                    ftp = new StoreOnFTPDecorator(ftp, ftpParams.getRetrievedFileDir());
            }
        }

        return ftp;
    }

    private static FTP getFtp(FTPParams ftpParams) throws IOException, GeneralSecurityException {
        FTPParams.FTPType ftpType = ftpParams.getFtpType();

        if (Objects.nonNull(ftpType)) {
            switch (ftpType) {
                case FTPS:
                    return new FTPSDecorator(prepareFtpsClient(ftpParams));
                case SFTP:
                    return new SFTPDecorator(prepareSFtpClient(ftpParams));
            }
        }

        if (FTPParams.FTPMethodStore.UNIQ.equals(ftpParams.getFtpMethodStore())) {
            return new FTPUniqDecorator(prepareFtpClient());
        }

        return new FTPDecorator(prepareFtpClient());
    }

    private static FTPClient prepareFtpClient() {
        FTPClient ftpClient = new FTPClient();
        return ftpClient;
    }

    private static SFTPClient prepareSFtpClient(FTPParams ftpParams) {
        SFTPClient sftpClient = new SFTPClient(ftpParams.getKeyStore(), ftpParams.getKeyPassword());
        return sftpClient;
    }

    private static FTPSClient prepareFtpsClient(FTPParams ftpParams) throws IOException, GeneralSecurityException {
        FTPSClient ftpsClient = new FTPSClient();
        ftpsClient.setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());
        String keyPassword = ftpParams.getKeyPassword();

        KeyStore keyStore = getKeyStore(ftpParams, keyPassword);

        KeyManager keyManager = KeyManagerUtils.createClientKeyManager(keyStore, null, keyPassword);
        ftpsClient.setKeyManager(keyManager);
        return ftpsClient;
    }

    private static KeyStore getKeyStore(FTPParams ftpParams, String keyPassword)
        throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        ByteArrayInputStream is = new ByteArrayInputStream(ftpParams.getKeyStore());
        KeyStore keyStore = KeyStore.getInstance(ftpParams.getKeyStoreType());
        keyStore.load(is, keyPassword.toCharArray());

        return keyStore;
    }
}
