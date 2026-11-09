package com.eurosoftit.ebok.web.rest.vacation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.I_AM_A_TEAPOT)
public class ValidationException extends RuntimeException {
    public ValidationException(String value) {
        super(value);
    }
}
