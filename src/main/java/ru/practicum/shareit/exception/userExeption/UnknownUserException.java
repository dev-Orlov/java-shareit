package ru.practicum.shareit.exception.userExeption;

public class UnknownUserException extends RuntimeException {

    public UnknownUserException(String s) {
        super(s);
    }
}
