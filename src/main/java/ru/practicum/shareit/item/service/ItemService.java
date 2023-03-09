package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingInfoDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long ownerId);

    ItemDto update(ItemDto itemDto, Long itemId, Long ownerId);

    ItemWithBookingInfoDto getItem(Long itemId, Long userId);

    List<ItemWithBookingInfoDto> getItemsByOwner(Long ownerId);

    List<ItemDto> searchItem(String text);
}
