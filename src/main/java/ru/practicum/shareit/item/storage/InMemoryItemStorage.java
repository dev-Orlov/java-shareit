package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.itemExeption.UnknownItemException;
import ru.practicum.shareit.exception.userExeption.UnknownUserException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.validator.ItemValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage{

    private final HashMap<Long, Item> items = new HashMap<>();
    private final ItemValidator itemValidator;

    @Override
    public Item create(Item item) {
        itemValidator.validate(item);

        items.put(item.getId(), item);
        log.debug("Создан объект вещи: {}", item);
        return item;
    }

    @Override
    public Item update(Item item, Long itemId, Long ownerId) {
        if (ownerId == null || !Objects.equals(items.get(itemId).getOwnerId(), ownerId)) {
            throw new UnknownUserException("редактировать вещь может только владелец");
        }

        if (!items.containsKey(itemId)) {
            log.error("вещи с id={} не существует", itemId);
            throw new UnknownItemException("попытка обновить несуществующую вещь");
        }

        if (item.getName() != null) {
            items.get(itemId).setName(item.getName());
        }

        if (item.getDescription() != null) {
            items.get(itemId).setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            items.get(itemId).setAvailable(item.getAvailable());
        }

        log.debug("Изменён объект вещи: {}", item);
        return items.get(itemId);
    }

    @Override
    public Item getItem(Long itemId) {
        if (!items.containsKey(itemId)) {
            log.error("вещи с id={} не существует", itemId);
            throw new UnknownItemException("попытка получить несуществующую вещь");
        }
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemsByOwner(Long ownerId) {
        List<Item> itemList = new ArrayList<>();

        for (Long itemId : items.keySet()) {
            if (items.get(itemId).getOwnerId() == ownerId) {
                itemList.add(items.get(itemId));
            }
        }
        return itemList;
    }

    @Override
    public List<Item> searchItem(String text) {
        List<Item> itemList = new ArrayList<>();
        String query = text.toLowerCase();

        if (query.trim().length() == 0) {
            return itemList;
        }

        for (Long itemId : items.keySet()) {
            if (items.get(itemId).getName().toLowerCase().contains(query) ||
                    items.get(itemId).getDescription().toLowerCase().contains(query) &&
                            items.get(itemId).getAvailable().equals(true)) {
                itemList.add(items.get(itemId));
            }
        }
        return itemList;
    }
}
