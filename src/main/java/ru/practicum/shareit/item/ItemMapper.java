package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemDto toItemDto(Item model);

    Item toItem(ItemDto dto);
}
