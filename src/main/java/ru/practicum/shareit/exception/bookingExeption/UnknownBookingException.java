package ru.practicum.shareit.exception.bookingExeption;

public class UnknownBookingException extends RuntimeException {

    public UnknownBookingException(String s) {
        super(s);
    }
}
