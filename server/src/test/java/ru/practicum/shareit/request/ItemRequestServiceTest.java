package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.requestException.UnknownRequestException;
import ru.practicum.shareit.exception.userExeption.UnknownUserException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {

    private final ItemRequestService itemRequestService;

    private final UserService userService;

    private final UserDto userDto1 = new UserDto(null, "пользователь1", "mail2@mail.com");
    private final UserDto userDto2 = new UserDto(null, "пользователь2", "mail3@mail.com");
    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "нужна вещь",
            LocalDateTime.of(2023, 9, 7, 10, 30, 5, 1),
            1L, new ArrayList<>());

    @Test
    @DisplayName("Тест создания запроса")
    void createTest() {
        UnknownUserException exp1 = assertThrows(UnknownUserException.class,
                () -> itemRequestService.create(itemRequestDto, -1L, null));
        assertEquals("попытка получить несуществующего пользователя", exp1.getMessage());

        UserDto user = userService.create(userDto1);

        ItemRequestDto checkItemRequestDto = itemRequestService.create(itemRequestDto, user.getId(),
                LocalDateTime.of(2023, 9, 7, 10, 30, 5, 1));
        assertEquals(itemRequestDto.getDescription(), checkItemRequestDto.getDescription());
        assertEquals(itemRequestDto.getCreated(), checkItemRequestDto.getCreated());
        assertEquals(itemRequestDto.getItems(), checkItemRequestDto.getItems());
    }

    @Test
    @DisplayName("Тест получения запросов владельцем")
    void getRequestsByOwnerTest() {
        UserDto user = userService.create(userDto1);
        assertEquals(new ArrayList<>(), itemRequestService.getRequestsByOwner(user.getId()));

        ItemRequestDto checkItemRequestDto = itemRequestService.create(itemRequestDto, user.getId(),
                LocalDateTime.of(2023, 9, 7, 10, 30, 5, 1));
        assertEquals(1, itemRequestService.getRequestsByOwner(user.getId()).size());
        assertEquals(itemRequestDto.getDescription(),
                itemRequestService.getRequestsByOwner(user.getId()).get(0).getDescription());
        assertEquals(itemRequestDto.getCreated(),
                itemRequestService.getRequestsByOwner(user.getId()).get(0).getCreated());
        assertEquals(itemRequestDto.getItems(),
                itemRequestService.getRequestsByOwner(user.getId()).get(0).getItems());
    }

    @Test
    @DisplayName("Тест получения всех запросов")
    void getAllTest() {
        UserDto user1 = userService.create(userDto1);
        UserDto user2 = userService.create(userDto2);
        assertEquals(new ArrayList<>(), itemRequestService.getAll(user1.getId(), 0, null));

        ItemRequestDto checkItemRequestDto = itemRequestService.create(itemRequestDto, user1.getId(),
                LocalDateTime.of(2023, 9, 7, 10, 30, 5, 1));
        assertEquals(1, itemRequestService.getAll(user2.getId(), 0, 10).size());
        assertEquals(itemRequestDto.getDescription(),
                itemRequestService.getAll(user2.getId(), 0, 10).get(0).getDescription());
        assertEquals(itemRequestDto.getCreated(),
                itemRequestService.getAll(user2.getId(), 0, 10).get(0).getCreated());
        assertEquals(itemRequestDto.getItems(),
                itemRequestService.getAll(user2.getId(), 0, 10).get(0).getItems());
    }

    @Test
    @DisplayName("Тест получения запроса")
    void getItemRequestTest() {
        UserDto user = userService.create(userDto1);
        UnknownRequestException exp1 = assertThrows(UnknownRequestException.class,
                () -> itemRequestService.getItemRequest(-1L, user.getId()));
        assertEquals("попытка получить несуществующий запрос", exp1.getMessage());

        ItemRequestDto checkItemRequestDto = itemRequestService.create(itemRequestDto, user.getId(),
                LocalDateTime.of(2023, 9, 7, 10, 30, 5, 1));
        assertEquals(itemRequestDto.getDescription(),
                itemRequestService.getItemRequest(checkItemRequestDto.getId(), user.getId()).getDescription());
        assertEquals(itemRequestDto.getCreated(),
                itemRequestService.getItemRequest(checkItemRequestDto.getId(), user.getId()).getCreated());
        assertEquals(itemRequestDto.getItems(),
                itemRequestService.getItemRequest(checkItemRequestDto.getId(), user.getId()).getItems());
    }
}
