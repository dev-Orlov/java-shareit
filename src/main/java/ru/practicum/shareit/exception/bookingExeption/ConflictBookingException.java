package ru.practicum.shareit.exception.bookingExeption;

public class ConflictBookingException extends RuntimeException {

    public ConflictBookingException(String s) {
        super(s);
    }
}
