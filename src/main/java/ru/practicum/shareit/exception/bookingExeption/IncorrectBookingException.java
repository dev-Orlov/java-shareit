package ru.practicum.shareit.exception.bookingExeption;

public class IncorrectBookingException extends RuntimeException {

    public IncorrectBookingException(String s) {
        super(s);
    }
}
