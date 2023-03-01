package ru.practicum.shareit.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.storage.UserStorage;

@Component
@Slf4j
@RequiredArgsConstructor
public class ItemValidator {

    private static long genItemId = 0;

    private final UserStorage userStorage;

    public void validate(Item item) {
        userStorage.getUser(item.getOwnerId()); // в случае, если владелец не существует, метод выдаст ошибку
        generateId(item);
    }

    public static void generateId(Item item) {
        if (item.getId() == null || item.getId() == 0) {
            item.setId(++genItemId);
        }
    }
}
