package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(CreatedBookingDto createdBookingDto, Long userId);

    BookingDto update(Long bookingId, Long bookerId, Boolean approved);

    BookingDto getBooking(Long bookingId, Long userId);

    List<BookingDto> getUserBookings(String state, Long userId, Integer from, Integer size);

    List<BookingDto> getOwnerBookings(String state, Long userId, Integer from, Integer size);

}
