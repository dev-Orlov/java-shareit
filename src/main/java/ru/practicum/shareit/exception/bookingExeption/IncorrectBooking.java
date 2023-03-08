package ru.practicum.shareit.exception.bookingExeption;

public class IncorrectBooking extends RuntimeException {

    public IncorrectBooking(String s) {
        super(s);
    }
}
