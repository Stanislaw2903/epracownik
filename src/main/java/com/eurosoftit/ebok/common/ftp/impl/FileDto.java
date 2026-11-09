package com.eurosoftit.ebok.common.ftp.impl;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class FileDto {
	private String name;
	private byte[] content;
}
