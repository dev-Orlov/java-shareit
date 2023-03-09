package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemWithBookingInfoDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemMapperWithBookingInfo {

    public ItemWithBookingInfoDto toItemWithBookingInfoDto(Item item, Booking lastBooking, Booking nextBooking,
                                                           List<CommentDto> comments) {
        return new ItemWithBookingInfoDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwnerId(),
                item.getRequestId() != null ? item.getRequestId() : null,
                lastBooking != null ? toShortBookingDto(lastBooking) : null,
                nextBooking != null ? toShortBookingDto(nextBooking) : null,
                comments != null ? comments : null
        );
    }

    private ShortBookingDto toShortBookingDto(Booking booking) {
        return new ShortBookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker().getId(),
                booking.getItem().getId(),
                booking.getStatus()
        );
    }
}
