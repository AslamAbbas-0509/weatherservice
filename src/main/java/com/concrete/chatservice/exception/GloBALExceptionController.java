package com.concrete.chatservice.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Component
public class GloBALExceptionController {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> getExceptionResponseHandler(Exception ex ,WebRequest webRequest)
	{
		return buildErrorResponse(HttpStatus.BAD_REQUEST , "This is a general exception", ex) ;
	}
	
	private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message, Exception ex) {
        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", status.value());
        errorDetails.put("error", status.getReasonPhrase());
        errorDetails.put("message", message);
        errorDetails.put("details", ex.getLocalizedMessage());

        return new ResponseEntity<>(errorDetails, status);
    }
}
