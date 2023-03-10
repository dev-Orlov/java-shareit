package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingInfoDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long ownerId);

    ItemDto update(ItemDto itemDto, Long itemId, Long ownerId);

    ItemWithBookingInfoDto getItem(Long itemId, Long userId);

    List<ItemWithBookingInfoDto> getItemsByOwner(Long ownerId);

    List<ItemDto> searchItem(String text);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long userId);

    List<Comment> getCommentsByItemId(Long itemId);
}
