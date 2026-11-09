package com.eurosoftit.ebok.web.rest.vacation;

import com.eurosoftit.ebok.common.ErrorDto;
import lombok.Data;

@Data
public class VacationException extends Exception {

    private ErrorDto error;

    public VacationException(String errorMessage, String message) {
        super(message);
        error = new ErrorDto();
        error.setError(errorMessage);
        error.setMessage(message);
        error.setTitle(errorMessage);
    }
}
