package com.eurosoftit.ebok.web.rest.vacation.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class LackOfFinalApproveException extends RuntimeException {
    LackOfFinalApproveException() {
        super("LackOfFinalApproveException");
    }
}
