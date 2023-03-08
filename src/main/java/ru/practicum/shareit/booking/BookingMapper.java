package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final ItemMapper itemMapper;
    private final ItemService itemService;
    private final UserMapper userMapper;
    private final UserService userService;

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                userMapper.toUserDto(booking.getBooker()),
                itemMapper.toItemDto(booking.getItem())
        );
    }


    public Booking toBooking(CreatedBookingDto createdBookingDto, Long bookerId) {
        return new Booking(
                -1L,  // присваиваем временный id
                createdBookingDto.getStart(),
                createdBookingDto.getEnd(),
                itemMapper.toItem(itemService.getItem(createdBookingDto.getItemId()), createdBookingDto.getItemId()),
                userMapper.toUser(userService.getUser(bookerId)),
                Status.WAITING
        );
    }

    public CreatedBookingDto toCreatedBookingDto(Booking booking, Long bookerId) {
        return new CreatedBookingDto(
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                booking.getBooker().getId(),
                booking.getItem().getId()
        );
    }
}
