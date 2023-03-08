package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingInfoDto;
import ru.practicum.shareit.item.model.Item;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    private final BookingService bookingService;

    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwnerId(),
                item.getRequestId() != null ? item.getRequestId() : null
        );
    }


    public Item toItem(ItemDto itemDto, Long ownerId) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                ownerId,
                itemDto.getRequestId() != null ? itemDto.getRequestId() : null
        );
    }

    public ItemWithBookingInfoDto toItemWithBookingInfoDto(Item item) {
        return new ItemWithBookingInfoDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwnerId(),
                item.getRequestId() != null ? item.getRequestId() : null,
                bookingService.getLastBooking(item.getId()),
                bookingService.getNextBooking(item.getId())
                //checker.getCommentsByItemId(item.getId())
        );
    }
}
