package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item create(Item item);

    Item update(Item item, Long itemId, Long ownerId);

    Item getItem(Long itemId);

    List<Item> getItemsByOwner(Long ownerId);

    List<Item> searchItem(String query);
}
