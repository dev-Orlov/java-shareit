package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.userExeption.ConflictUserException;
import ru.practicum.shareit.exception.userExeption.UnknownUserException;
import ru.practicum.shareit.exception.userExeption.UserValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {

    private final UserService userService;
    private final UserDto userDto = new UserDto(2L, "пользователь2", "mail3@mail.com");

    @Test
    @DisplayName("Тест получения пользователя")
    void getUserTest() {
        UnknownUserException exp1 = assertThrows(UnknownUserException.class,
                () -> userService.getUser(-1L));
        assertEquals("попытка получить несуществующего пользователя", exp1.getMessage());

        UserDto checkUser = userService.create(userDto);
        assertEquals(checkUser, userService.getUser(checkUser.getId()));
    }

    @Test
    @DisplayName("Тест получения всех пользователей")
    void getAllTest() {
        assertEquals(new ArrayList<>(), userService.getAll());

        UserDto checkUser = userService.create(userDto);
        assertEquals(checkUser, userService.getAll().get(0));
    }

    @Test
    @DisplayName("Тест создания пользователя")
    void createTest() {
        UserValidationException exp1 = assertThrows(UserValidationException.class,
                () -> userService.create(new UserDto(10L, "пользователь2", null)));
        assertEquals("регистрация пользователя без указания email невозможна", exp1.getMessage());

        UserDto checkUser = userService.create(userDto);
        assertEquals(checkUser, userService.getUser(checkUser.getId()));

        ConflictUserException exp2 = assertThrows(ConflictUserException.class,
                () -> userService.create(new UserDto(10L, "пользователь2", "mail3@mail.com")));
        assertEquals("пользователь с таким email уже существует", exp2.getMessage());
    }

    @Test
    @DisplayName("Тест обновления пользователя")
    void updateTest() {
        UnknownUserException exp1 = assertThrows(UnknownUserException.class,
                () -> userService.update(userDto, -1L));
        assertEquals("попытка обновить несуществующего пользователя", exp1.getMessage());

        UserDto checkUser = userService.create(userDto);

        UserValidationException exp2 = assertThrows(UserValidationException.class,
                () -> userService.update(new UserDto(2L, "пользователь 2", "mail3@mail.com"),
                        checkUser.getId()));
        assertEquals("логин не может содержать пробелы", exp2.getMessage());

        UserDto update = new UserDto(checkUser.getId(), "обновление", "mail2223@yandex.ru");

        UserDto updatedUser = userService.update(update, checkUser.getId());
        assertEquals(updatedUser, userService.getUser(update.getId()));
    }

    @Test
    @DisplayName("Тест удаления пользователя")
    void removeTest() {
        UserDto checkUser = userService.create(userDto);
        assertNull(userService.remove(checkUser.getId()));
    }
}
