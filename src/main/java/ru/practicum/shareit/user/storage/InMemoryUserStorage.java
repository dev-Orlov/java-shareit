package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.userExeption.ConflictUserException;
import ru.practicum.shareit.exception.userExeption.UnknownUserException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.validator.UserValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Long, User> users = new HashMap<>();

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(Long userId) {
        if (!users.containsKey(userId)) {
            log.error("пользователя с id={} не существует", userId);
            throw new UnknownUserException("попытка получить несуществующего пользователя");
        }
        return users.get(userId);
    }

    @Override
    public User create(User user) {
        checkEmailExist(user.getEmail());
        UserValidator.validate(user);

        users.put(user.getId(), user);
        log.debug("Создан объект пользователя: {}", user);
        return user;
    }

    @Override
    public User update(User user, Long userId) {
        if (user.getName() != null) {
            UserValidator.loginValidate(user);
        }

        checkEmailExist(user.getEmail());

        if (!users.containsKey(userId)) {
            log.error("пользователя с id={} не существует", userId);
            throw new UnknownUserException("попытка обновить несуществующего пользователя");
        }

        if (user.getName() != null) {
            users.get(userId).setName(user.getName());
        }

        if (user.getEmail() != null) {
            users.get(userId).setEmail(user.getEmail());
        }

        log.debug("Изменён объект пользователя: {}", user);
        return users.get(userId);
    }

    private void checkEmailExist(String email) {
        if (email != null) {
            for (long userId : users.keySet()) {
                if (users.get(userId).getEmail().equals(email)) {
                    throw new ConflictUserException("пользователь с таким email уже существует");
                }
            }
        }
    }

    @Override
    public User remove(Long userId) {
        if (!users.containsKey(userId)) {
            log.error("пользователя с id={} не существует", userId);
            throw new UnknownUserException("попытка удалить несуществующего пользователя");
        }
        User removedUser = users.get(userId);
        users.remove(userId);
        log.debug("Удалён объект пользователя: {}", removedUser);
        return removedUser;
    }
}
