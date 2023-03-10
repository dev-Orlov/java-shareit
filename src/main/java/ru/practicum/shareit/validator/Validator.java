package ru.practicum.shareit.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;
import ru.practicum.shareit.exception.bookingExeption.IncorrectBookingException;
import ru.practicum.shareit.exception.itemExeption.UnavailableItemException;
import ru.practicum.shareit.exception.itemExeption.UnknownItemException;
import ru.practicum.shareit.exception.userExeption.UnknownUserException;
import ru.practicum.shareit.exception.userExeption.UserValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Component
@Slf4j
@RequiredArgsConstructor
public class Validator {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    public void checkUserExist(Item item) {
        if (userRepository.findById(item.getOwnerId()).isEmpty()) {
            log.error("пользователя с id={} не существует", item.getOwnerId());
            throw new UnknownUserException("попытка получить несуществующего пользователя");
        }
    }

    public void userValidate(User user) {
        loginValidate(user);
        if (user.getEmail() == null) {
            throw new UserValidationException("регистрация пользователя без указания email невозможна");
        }
    }

    public void loginValidate(User user) {
        if (user.getName().contains(" ")) {
            log.error("логин не может содержать пробелы: {}", user.getName());
            throw new UserValidationException("логин не может содержать пробелы");
        }
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getName());
        }
    }

    public void checkUserExistById(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.error("пользователя с id={} не существует", userId);
            throw new UnknownUserException("попытка получить несуществующего пользователя");
        }
    }

    public void checkItemExistById(Long itemId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            log.error("вещи с id={} не существует", itemId);
            throw new UnknownItemException("попытка получить несуществующую вещь");
        }
    }

    public void checkItemAvailable(Long itemId) {
        if (itemRepository.findById(itemId).isPresent() && !itemRepository.findById(itemId).get().getAvailable()) {
            log.error("вещь с id={} недоступна для бронирования", itemId);
            throw new UnavailableItemException("попытка забронировать недоступную вещь");
        }
    }

    public void checkBookingTime(CreatedBookingDto createdBookingDto) {
        if (createdBookingDto.getEnd().isBefore(createdBookingDto.getStart())) {
            log.error("Время окончания брони не может быть раньше начала бронирования");
            throw new IncorrectBookingException("дата старта брони должна быть раньше, чем дата окончания");
        }
    }
}
