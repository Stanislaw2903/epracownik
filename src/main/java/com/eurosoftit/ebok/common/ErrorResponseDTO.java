package com.eurosoftit.ebok.common;

import lombok.Data;

@Data
public class ErrorResponseDTO {
    private String title;
    private String error;
    private String message;
}
