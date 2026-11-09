package com.eurosoftit.ebok.common;

import com.eurosoftit.ebok.web.rest.vacation.service.LackOfFinalApproveException;
import com.eurosoftit.ebok.web.rest.vacation.service.LackOfInitialApproveException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AdviceController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value
        = {LackOfFinalApproveException.class, LackOfInitialApproveException.class})
    protected ResponseEntity<ErrorResponseDTO> handleConflict(
        RuntimeException ex, WebRequest request) {

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
        errorResponseDTO.setTitle("ebokApp.vacations.vacationApplicationResponse.title");
        errorResponseDTO.setError("test");
        errorResponseDTO.setMessage(ex.getMessage());
        return new ResponseEntity(errorResponseDTO, HttpStatus.I_AM_A_TEAPOT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponseDTO> illegalArgumentException(
        RuntimeException ex, WebRequest request) {

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
        errorResponseDTO.setTitle("error.permissions");
        errorResponseDTO.setError("error.permissions");
        errorResponseDTO.setMessage(ex.getMessage());
        return new ResponseEntity(errorResponseDTO, HttpStatus.CONFLICT);
    }
}
