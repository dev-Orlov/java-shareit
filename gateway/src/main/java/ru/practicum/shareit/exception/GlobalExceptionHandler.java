package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ErrorObject> catchResourceBadRequestException(IllegalArgumentException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorObject(e.getMessage()), HttpStatus.BAD_REQUEST); //код 400
    }
}
