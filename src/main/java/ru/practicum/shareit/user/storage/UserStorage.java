package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getAll();

    User getUser(Long userId);

    User create(User user);

    User update(User user, Long userId);

    User remove(Long userId);
}
