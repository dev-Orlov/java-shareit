package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.shareit.exception.bookingExeption.ConflictBookingException;
import ru.practicum.shareit.exception.bookingExeption.IncorrectBooking;
import ru.practicum.shareit.exception.bookingExeption.UnknownBookingException;
import ru.practicum.shareit.exception.itemExeption.UnavailableItemException;
import ru.practicum.shareit.exception.itemExeption.UnknownItemException;
import ru.practicum.shareit.exception.userExeption.ConflictUserException;
import ru.practicum.shareit.exception.userExeption.UnknownUserException;
import ru.practicum.shareit.exception.userExeption.UserValidationException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({UnknownUserException.class, UnknownItemException.class, UnknownBookingException.class})
    public ResponseEntity<AppError> catchResourceNotFoundException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({UserValidationException.class, UnavailableItemException.class, IncorrectBooking.class})
    public ResponseEntity<ErrorObject> catchResourceBadRequestException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorObject(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ConflictUserException.class, ConflictBookingException.class})
    public ResponseEntity<AppError> catchConflictException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new AppError(HttpStatus.CONFLICT.value(),
                e.getMessage()), HttpStatus.CONFLICT);
    }
}
