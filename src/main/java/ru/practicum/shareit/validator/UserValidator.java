package ru.practicum.shareit.validator;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.userExeption.UserValidationException;
import ru.practicum.shareit.user.User;

@Slf4j
public class UserValidator {

    private static long genId = 0;

    public static void validate(User user) {
        loginValidate(user);
        if (user.getEmail() == null) {
            throw new UserValidationException("регистрация пользователя без указания email невозможна");
        }

        if (user.getId() == null || user.getId() == 0) {
            user.setId(++genId);
        }
    }

    public static void loginValidate(User user) {
        if (user.getName().contains(" ")) {
            log.error("логин не может содержать пробелы: {}", user.getName());
            throw new UserValidationException("логин не может содержать пробелы");
        }
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getName());
        }
    }
}
