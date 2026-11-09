package com.eurosoftit.ebok.common.ftp.impl;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class FTPSClient extends org.apache.commons.net.ftp.FTPSClient {
    FTPSClient(){
        super();
    }
}
