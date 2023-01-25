package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {

    private final ItemStorage itemStorage;
    private final ItemMapper itemMapper;

    public ItemDto create(ItemDto itemDto, Long ownerId) {
        itemDto.setOwnerId(ownerId);
        return itemMapper.toItemDto(itemStorage.create(itemMapper.toItem(itemDto)));
    }

    public ItemDto update(ItemDto itemDto, Long itemId, Long ownerId) {
        itemDto.setOwnerId(ownerId);
        return itemMapper.toItemDto(itemStorage.update(itemMapper.toItem(itemDto), itemId, ownerId));
    }

    public ItemDto getItem(Long itemId) {
        return itemMapper.toItemDto(itemStorage.getItem(itemId));
    }

    public List<ItemDto> getItemsByOwner(Long ownerId) {
        return itemStorage.getItemsByOwner(ownerId).stream()
                .map(itemMapper::toItemDto)
                .collect(toList());
    }

    public List<ItemDto> searchItem(String text) {
        return itemStorage.searchItem(text).stream()
                .map(itemMapper::toItemDto)
                .collect(toList());
    }
}
