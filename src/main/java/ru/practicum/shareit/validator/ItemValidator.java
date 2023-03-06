package ru.practicum.shareit.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.userExeption.UnknownUserException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.storage.UserStorage;

@Component
@Slf4j
@RequiredArgsConstructor
public class ItemValidator {

    private final UserRepository userRepository;


    public void validate(Item item) {
        if (userRepository.findById(item.getOwnerId()).isEmpty()) {
            log.error("пользователя с id={} не существует", item.getOwnerId());
            throw new UnknownUserException("попытка получить несуществующего пользователя");
        }
    }

}
