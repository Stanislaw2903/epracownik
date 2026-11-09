package com.eurosoftit.ebok.common.ftp.impl;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class FTPClient extends org.apache.commons.net.ftp.FTPClient {
    FTPClient(){
        super();
    }
}
