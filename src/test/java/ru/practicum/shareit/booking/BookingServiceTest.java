package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.bookingExeption.IncorrectBookingException;
import ru.practicum.shareit.exception.bookingExeption.UnknownBookingException;
import ru.practicum.shareit.exception.itemExeption.UnavailableItemException;
import ru.practicum.shareit.exception.itemExeption.UnknownItemException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {

    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;
    private final UserDto userDto1 = new UserDto(1L, "пользователь1", "mail2@mail.com");
    private final UserDto userDto2 = new UserDto(2L, "пользователь2", "mail3@mail.com");

    @Test
    @DisplayName("Тест создания бронирования")
    void createTest() {
        UserDto owner = userService.create(userDto1);
        UserDto booker = userService.create(userDto2);
        ItemDto item = itemService.create((new ItemDto(null, "вещь1", "описание 1", true,
                owner.getId(), null)), owner.getId());

        CreatedBookingDto createdBookingDto1 = new CreatedBookingDto(LocalDateTime
                .of(2023, 9, 1, 10, 30, 5, 1),
                LocalDateTime.of(2023, 9, 7, 10, 30, 5, 1),
                null, booker.getId(), item.getId());

        UnknownBookingException exp1 = assertThrows(UnknownBookingException.class,
                () -> bookingService.create(createdBookingDto1, owner.getId()));
        assertEquals("попытка забронировать свою вещь", exp1.getMessage());


        BookingDto bookingDto1 = bookingService.create(createdBookingDto1, booker.getId());
        BookingDto bookingDto = new BookingDto(bookingDto1.getId(),
                LocalDateTime.of(2023, 9, 1, 10, 30, 5, 1),
                LocalDateTime.of(2023, 9, 7, 10, 30, 5, 1),
                Status.WAITING, booker, item);
        BookingDto testBookingDto = bookingService.getBooking(bookingDto1.getId(), owner.getId());
        assertEquals(bookingDto, testBookingDto);

        IncorrectBookingException exp2 = assertThrows(IncorrectBookingException.class,
                () -> bookingService.create(createdBookingDto1, booker.getId()));
        assertEquals("попытка повторно создать бронирование", exp2.getMessage());

        CreatedBookingDto wrongBookingDto1 = new CreatedBookingDto(LocalDateTime
                .of(2023, 9, 1, 10, 30, 5, 1),
                LocalDateTime.of(2023, 9, 7, 10, 30, 5, 1),
                null, booker.getId(), -1L);

        UnknownItemException exp3 = assertThrows(UnknownItemException.class,
                () -> bookingService.create(wrongBookingDto1, owner.getId()));
        assertEquals("попытка получить несуществующую вещь", exp3.getMessage());

        ItemDto wrongItem = itemService.create((new ItemDto(null, "вещь2", "описание 2",
                false, owner.getId(), null)), owner.getId());

        CreatedBookingDto wrongBookingDto2 = new CreatedBookingDto(LocalDateTime
                .of(2023, 9, 1, 10, 30, 5, 1),
                LocalDateTime.of(2023, 9, 7, 10, 30, 5, 1),
                null, booker.getId(), wrongItem.getId());

        UnavailableItemException exp4 = assertThrows(UnavailableItemException.class,
                () -> bookingService.create(wrongBookingDto2, owner.getId()));
        assertEquals("попытка забронировать недоступную вещь", exp4.getMessage());
    }

    @Test
    @DisplayName("Тест обновления бронирования")
    void updateTest() {
        UserDto owner = userService.create(userDto1);
        UserDto booker = userService.create(userDto2);
        ItemDto item = itemService.create((new ItemDto(null, "вещь1", "описание 1", true,
                owner.getId(), null)), owner.getId());

        CreatedBookingDto createdBookingDto1 = new CreatedBookingDto(LocalDateTime
                .of(2023, 9, 1, 10, 30, 5, 1),
                LocalDateTime.of(2023, 9, 7, 10, 30, 5, 1),
                null, booker.getId(), item.getId());
        BookingDto bookingDto1 = bookingService.create(createdBookingDto1, booker.getId());

        UnknownBookingException exp1 = assertThrows(UnknownBookingException.class,
                () -> bookingService.update(-1L, booker.getId(), true));
        assertEquals("попытка изменить статус несуществующего бронирования", exp1.getMessage());

        UnknownBookingException exp2 = assertThrows(UnknownBookingException.class,
                () -> bookingService.update(bookingDto1.getId(), booker.getId(), true));
        assertEquals("попытка подтвердить чужое бронирование", exp2.getMessage());

        BookingDto updatedBookingDto = bookingService.update(bookingDto1.getId(), owner.getId(), true);
        assertEquals(updatedBookingDto.getStatus(), Status.APPROVED);

        IncorrectBookingException exp3 = assertThrows(IncorrectBookingException.class,
                () -> bookingService.update(bookingDto1.getId(), owner.getId(), true));
        assertEquals("попытка повторно подтвердить бронирование", exp3.getMessage());
    }

    @Test
    @DisplayName("Тест получения бронирования по id")
    void getBookingTest() {
        UserDto owner = userService.create(userDto1);
        UserDto booker = userService.create(userDto2);
        UserDto user3 = userService.create(new UserDto(null, "пользователь10", "mail10@mail.com"));
        ItemDto item = itemService.create((new ItemDto(null, "вещь1", "описание 1", true,
                owner.getId(), null)), owner.getId());

        CreatedBookingDto createdBookingDto1 = new CreatedBookingDto(LocalDateTime
                .of(2023, 9, 1, 10, 30, 5, 1),
                LocalDateTime.of(2023, 9, 7, 10, 30, 5, 1),
                null, booker.getId(), item.getId());
        BookingDto bookingDto1 = bookingService.create(createdBookingDto1, booker.getId());
        BookingDto bookingDto = new BookingDto(bookingDto1.getId(),
                LocalDateTime.of(2023, 9, 1, 10, 30, 5, 1),
                LocalDateTime.of(2023, 9, 7, 10, 30, 5, 1),
                Status.WAITING, booker, item);
        assertEquals(bookingDto, bookingDto1);

        UnknownBookingException exp1 = assertThrows(UnknownBookingException.class,
                () -> bookingService.getBooking(-1L, booker.getId()));
        assertEquals("попытка получить несуществующее бронирование", exp1.getMessage());

        UnknownBookingException exp2 = assertThrows(UnknownBookingException.class,
                () -> bookingService.getBooking(bookingDto1.getId(), user3.getId()));
        assertEquals("попытка получить чужое бронирование", exp2.getMessage());
    }

    @Test
    @DisplayName("Тест получения бронирований пользователя")
    void getUserBookingsTest() {
        UserDto owner = userService.create(userDto1);
        UserDto booker = userService.create(userDto2);
        UserDto user3 = userService.create(new UserDto(null, "пользователь10", "mail10@mail.com"));
        ItemDto item1 = itemService.create((new ItemDto(null, "вещь1", "описание 1", true,
                owner.getId(), null)), owner.getId());
        ItemDto item2 = itemService.create((new ItemDto(null, "вещь2", "описание 2", true,
                user3.getId(), null)), user3.getId());

        assertEquals(new ArrayList<>(), bookingService.getUserBookings("ALL",
                owner.getId(), 0, 10));

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

        assertEquals(2, bookingService.getUserBookings("ALL",
                booker.getId(), 0, 10).size());
        assertEquals(bookingDto2, bookingService.getUserBookings("WAITING",
                booker.getId(), 0, 10).get(0));
        assertEquals(bookingDto1, bookingService.getUserBookings("FUTURE",
                booker.getId(), 0, 10).get(1));
        assertEquals(new ArrayList<>(), bookingService.getUserBookings("PAST",
                booker.getId(), 0, 10));
        assertEquals(new ArrayList<>(), bookingService.getUserBookings("REJECTED",
                booker.getId(), 0, null));
        assertEquals(new ArrayList<>(), bookingService.getUserBookings("CURRENT",
                booker.getId(), 0, null));

        IncorrectBookingException exp1 = assertThrows(IncorrectBookingException.class,
                () -> bookingService.getUserBookings("INCORRECT", booker.getId(), 0, 10));
        assertEquals("Unknown state: INCORRECT", exp1.getMessage());
    }

    @Test
    @DisplayName("Тест получения бронирований для вещей владельца")
    void getOwnerBookingsTest() {
        UserDto owner = userService.create(userDto1);
        UserDto booker = userService.create(userDto2);
        UserDto user3 = userService.create(new UserDto(null, "пользователь10", "mail10@mail.com"));
        ItemDto item1 = itemService.create((new ItemDto(null, "вещь1", "описание 1", true,
                owner.getId(), null)), owner.getId());
        ItemDto item2 = itemService.create((new ItemDto(null, "вещь2", "описание 2", true,
                user3.getId(), null)), owner.getId());

        assertEquals(new ArrayList<>(), bookingService.getOwnerBookings("ALL",
                owner.getId(), 0, 10));

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

        assertEquals(2, bookingService.getOwnerBookings("ALL",
                owner.getId(), 0, 10).size());
        assertEquals(bookingDto2, bookingService.getOwnerBookings("WAITING",
                owner.getId(), 0, 10).get(0));
        assertEquals(bookingDto1, bookingService.getOwnerBookings("FUTURE",
                owner.getId(), 0, 10).get(1));
        assertEquals(new ArrayList<>(), bookingService.getOwnerBookings("PAST",
                booker.getId(), 0, 10));
        assertEquals(new ArrayList<>(), bookingService.getOwnerBookings("REJECTED",
                booker.getId(), 0, null));
        assertEquals(new ArrayList<>(), bookingService.getOwnerBookings("CURRENT",
                booker.getId(), 0, null));

        IncorrectBookingException exp1 = assertThrows(IncorrectBookingException.class,
                () -> bookingService.getOwnerBookings("INCORRECT", booker.getId(), 0, 10));
        assertEquals("Unknown state: INCORRECT", exp1.getMessage());
    }
}
