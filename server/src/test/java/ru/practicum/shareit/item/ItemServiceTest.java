package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.itemExeption.UnknownItemException;
import ru.practicum.shareit.exception.userExeption.UnknownUserException;
import ru.practicum.shareit.exception.userExeption.UserValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingInfoDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {

    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final ItemRequestService itemRequestService;
    private final UserDto userDto1 = new UserDto(null, "пользователь1", "mail2@mail.com");
    private final UserDto userDto2 = new UserDto(null, "пользователь2", "mail3@mail.com");
    private final ItemDto itemDto1 = new ItemDto(null, "вещь1", "описание 1", true,
            userDto1.getId(), null);
    private final ItemDto itemDto2 = new ItemDto(null, "вещь2", "описание 2", true,
            userDto2.getId(), null);

    @Test
    @DisplayName("Тест создания вещи")
    void createTest() {
        UnknownUserException exp1 = assertThrows(UnknownUserException.class,
                () -> itemService.create(itemDto1, -1L));
        assertEquals("попытка получить несуществующего пользователя", exp1.getMessage());

        UserDto owner = userService.create(userDto1);
        ItemDto item = itemService.create(itemDto1, owner.getId());
        ItemWithBookingInfoDto checkItem = new ItemWithBookingInfoDto(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable(), item.getOwnerId(), null, null,
                null, new ArrayList<>());

        assertEquals(checkItem, itemService.getItem(item.getId(), owner.getId()));
    }

    @Test
    @DisplayName("Тест обновления вещи")
    void updateTest() {
        UserDto owner = userService.create(userDto1);
        UserDto booker = userService.create(userDto2);
        ItemDto item = itemService.create(itemDto1, owner.getId());

        UnknownItemException exp1 = assertThrows(UnknownItemException.class,
                () -> itemService.update(itemDto1, -1L, userDto1.getId()));
        assertEquals("попытка обновить несуществующую вещь", exp1.getMessage());

        UnknownUserException exp2 = assertThrows(UnknownUserException.class,
                () -> itemService.update(item, item.getId(), booker.getId()));
        assertEquals("редактировать вещь может только владелец", exp2.getMessage());

        ItemDto itemToUpdate = new ItemDto(null, "новое имя", "измененное описание", false,
                userDto1.getId(), null);
        itemService.update(itemToUpdate, item.getId(), owner.getId());
        ItemWithBookingInfoDto checkItem = new ItemWithBookingInfoDto(item.getId(), itemToUpdate.getName(),
                itemToUpdate.getDescription(), itemToUpdate.getAvailable(), item.getOwnerId(), null,
                null, null, new ArrayList<>());

        assertEquals(checkItem, itemService.getItem(item.getId(), owner.getId()));
    }

    @Test
    @DisplayName("Тест получения вещи")
    void getItemTest() {
        UnknownItemException exp1 = assertThrows(UnknownItemException.class,
                () -> itemService.getItem(-1L, userDto1.getId()));

        assertEquals("попытка получить несуществующую вещь", exp1.getMessage());

        UserDto owner = userService.create(userDto1);
        ItemDto item1 = itemService.create(itemDto1, owner.getId());
        ItemDto item2 = itemService.create(itemDto2, owner.getId());
        ItemWithBookingInfoDto checkItem1 = new ItemWithBookingInfoDto(item1.getId(), item1.getName(),
                item1.getDescription(), item1.getAvailable(), item1.getOwnerId(), null, null,
                null, new ArrayList<>());

        assertEquals(checkItem1, itemService.getItem(item1.getId(), owner.getId()));

        UserDto booker = userService.create(userDto2);
        CreatedBookingDto createdBookingDto1 = new CreatedBookingDto(LocalDateTime
                .of(2023, 9, 1, 10, 30, 5, 1),
                LocalDateTime.of(2023, 9, 7, 10, 30, 5, 1),
                null, booker.getId(), item1.getId());
        CreatedBookingDto createdBookingDto2 = new CreatedBookingDto(LocalDateTime
                .of(2024, 9, 1, 10, 30, 5, 1),
                LocalDateTime.of(2024, 9, 7, 10, 30, 5, 1),
                null, booker.getId(), item2.getId());
        BookingDto bookingDto1 = bookingService.create(createdBookingDto1, booker.getId());
        BookingDto bookingDto2 = bookingService.create(createdBookingDto2, booker.getId());
        bookingService.update(bookingDto1.getId(), owner.getId(), true);
        bookingService.update(bookingDto2.getId(), owner.getId(), true);

        ItemWithBookingInfoDto checkItem2 = new ItemWithBookingInfoDto(item1.getId(), item1.getName(),
                item1.getDescription(), item1.getAvailable(), item1.getOwnerId(), null, null,
                new ShortBookingDto(bookingDto1.getId(), LocalDateTime.of(2023, 9, 1, 10,
                        30, 5, 1), LocalDateTime.of(2023, 9, 7,
                        10, 30, 5, 1), booker.getId(), item1.getId(), Status.APPROVED),
                new ArrayList<>());

        assertEquals(checkItem2, itemService.getItem(item1.getId(), owner.getId()));
    }

    @Test
    @DisplayName("Тест получения вещей владельцем")
    void getItemsByOwnerTest() {
        UserDto owner = userService.create(userDto1);

        assertEquals(new ArrayList<>(), itemService.getItemsByOwner(owner.getId(), 0, 10));

        ItemDto item1 = itemService.create(itemDto1, owner.getId());
        ItemDto item2 = itemService.create(itemDto2, owner.getId());

        ItemWithBookingInfoDto checkItem1 = new ItemWithBookingInfoDto(item1.getId(), item1.getName(),
                item1.getDescription(), item1.getAvailable(), item1.getOwnerId(), null, null,
                null, new ArrayList<>());
        ItemWithBookingInfoDto checkItem2 = new ItemWithBookingInfoDto(item2.getId(), item2.getName(),
                item2.getDescription(), item2.getAvailable(), item2.getOwnerId(), null, null,
                null, new ArrayList<>());

        assertEquals(checkItem1, itemService.getItemsByOwner(owner.getId(), 0, 10).get(0));
        assertEquals(checkItem2, itemService.getItemsByOwner(owner.getId(), 0, 10).get(1));
        assertEquals(2, itemService.getItemsByOwner(owner.getId(), 0, 10).size());

        UserDto booker = userService.create(userDto2);
        CreatedBookingDto createdBookingDto1 = new CreatedBookingDto(LocalDateTime
                .of(2023, 9, 1, 10, 30, 5, 1),
                LocalDateTime.of(2023, 9, 7, 10, 30, 5, 1),
                null, booker.getId(), item1.getId());
        CreatedBookingDto createdBookingDto2 = new CreatedBookingDto(LocalDateTime
                .of(2024, 9, 1, 10, 30, 5, 1),
                LocalDateTime.of(2024, 9, 7, 10, 30, 5, 1),
                null, booker.getId(), item2.getId());
        BookingDto bookingDto1 = bookingService.create(createdBookingDto1, booker.getId());
        BookingDto bookingDto2 = bookingService.create(createdBookingDto2, booker.getId());
        bookingService.update(bookingDto1.getId(), owner.getId(), true);
        bookingService.update(bookingDto2.getId(), owner.getId(), true);

        ItemWithBookingInfoDto checkItem3 = new ItemWithBookingInfoDto(item1.getId(), item1.getName(),
                item1.getDescription(), item1.getAvailable(), item1.getOwnerId(), null, null,
                new ShortBookingDto(bookingDto1.getId(), LocalDateTime.of(2023, 9, 1, 10,
                        30, 5, 1), LocalDateTime.of(2023, 9, 7,
                        10, 30, 5, 1), booker.getId(), item1.getId(), Status.APPROVED),
                new ArrayList<>());

        assertEquals(checkItem3, itemService.getItemsByOwner(owner.getId(), 0, null).get(0));
    }

    @Test
    @DisplayName("Тест поиска вещей")
    void searchItemTest() {
        UserDto owner = userService.create(userDto1);

        assertEquals(new ArrayList<>(), itemService.searchItem("вещь", 0, 10));

        ItemDto item1 = itemService.create(itemDto1, owner.getId());
        ItemDto item2 = itemService.create(itemDto2, owner.getId());

        assertEquals(item1, itemService.searchItem("вещь", 0, 10).get(0));
        assertEquals(item2, itemService.searchItem("вещь", 0, null).get(1));
        assertEquals(2, itemService.searchItem("вещь", 0, 10).size());
    }

    @Test
    @DisplayName("Тест создания комментария")
    void createCommentTest() throws InterruptedException {
        UserDto owner = userService.create(userDto1);
        UserDto booker = userService.create(userDto2);
        ItemDto item = itemService.create(itemDto1, owner.getId());
        CreatedBookingDto createdBookingDto1 = new CreatedBookingDto(LocalDateTime.now().plusSeconds(2),
                LocalDateTime.now().plusSeconds(4), null, booker.getId(), item.getId());

        CommentDto commentDto1 = new CommentDto(null, "комментарий 1", itemMapper.toItem(item, owner.getId()),
                owner.getName(), LocalDateTime.now());

        UserValidationException exp1 = assertThrows(UserValidationException.class,
                () -> itemService.createComment(commentDto1, item.getId(), owner.getId()));

        assertEquals("комментарий можно оставить только после завершения брони", exp1.getMessage());

        BookingDto bookingDto1 = bookingService.create(createdBookingDto1, booker.getId());
        bookingService.update(bookingDto1.getId(), owner.getId(), true);
        Thread.sleep(8000);

        CommentDto commentDto2 = new CommentDto(null, "комментарий 1", itemMapper.toItem(item, owner.getId()),
                owner.getName(), LocalDateTime.now());
        itemService.createComment(commentDto2, item.getId(), booker.getId());
        Assertions.assertEquals(1, itemService.getCommentsByItemId(item.getId()).size());
    }

    @Test
    @DisplayName("Тест получения комментариев")
    void getCommentsByItemIdTest() throws InterruptedException {
        UserDto owner = userService.create(userDto1);
        UserDto booker = userService.create(userDto2);
        ItemDto item = itemService.create(itemDto1, owner.getId());
        CreatedBookingDto createdBookingDto1 = new CreatedBookingDto(LocalDateTime.now().plusSeconds(2),
                LocalDateTime.now().plusSeconds(4), null, booker.getId(), item.getId());

        BookingDto bookingDto1 = bookingService.create(createdBookingDto1, booker.getId());
        bookingService.update(bookingDto1.getId(), owner.getId(), true);
        Thread.sleep(8000);

        CommentDto commentDto1 = new CommentDto(null, "комментарий 1", itemMapper.toItem(item, owner.getId()),
                booker.getName(), LocalDateTime.now());
        itemService.createComment(commentDto1, item.getId(), booker.getId());
        Assertions.assertEquals(1, itemService.getCommentsByItemId(item.getId()).size());
        Assertions.assertEquals(commentDto1.getText(), commentMapper.toCommentDto(itemService
                .getCommentsByItemId(item.getId()).get(0)).getText());
        Assertions.assertEquals(commentDto1.getAuthorName(), commentMapper.toCommentDto(itemService
                .getCommentsByItemId(item.getId()).get(0)).getAuthorName());
        Assertions.assertEquals(commentDto1.getItem(), commentMapper.toCommentDto(itemService
                .getCommentsByItemId(item.getId()).get(0)).getItem());
    }

    @Test
    @DisplayName("Тест получения вещей по запросу")
    void getItemsByRequestTest() throws InterruptedException {
        UserDto owner = userService.create(userDto1);
        UserDto booker = userService.create(userDto2);
        ItemDto item = itemService.create(new ItemDto(null, "вещь5", "описание 5", true,
                userDto1.getId(), 1L), owner.getId());
        List<ItemDto> itemList = List.of(item);
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "нужна вещь", null, booker.getId(),
                itemList);
        ItemRequestDto checkRequest = itemRequestService.create(itemRequestDto, booker.getId(), LocalDateTime.now());

        Assertions.assertEquals(itemList, itemService.getItemsByRequest(checkRequest.getId()));
    }
}
