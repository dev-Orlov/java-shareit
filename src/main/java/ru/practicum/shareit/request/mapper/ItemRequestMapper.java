package ru.practicum.shareit.request.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {

    private final ItemService itemService;

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemRequest.getUserId(),
                itemService.getItemsByRequest(itemRequest.getId())
        );
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto, Long userId, LocalDateTime created) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                created,
                userId
        );
    }
}
