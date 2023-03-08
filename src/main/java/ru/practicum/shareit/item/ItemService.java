package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.itemExeption.UnknownItemException;
import ru.practicum.shareit.exception.userExeption.UnknownUserException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.validator.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {

    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final Validator validator;
    private final BookingRepository bookingRepository;

    public ItemDto create(ItemDto itemDto, Long ownerId) {
        Item item = itemMapper.toItem(itemDto, ownerId);
        validator.checkUserExist(item);

        log.debug("Создан объект вещи: {}", item);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    public ItemDto update(ItemDto itemDto, Long itemId, Long ownerId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            log.error("вещи с id={} не существует", itemId);
            throw new UnknownItemException("попытка обновить несуществующую вещь");
        }

        Item item = itemRepository.findById(itemId).get();

        if (ownerId == null || !Objects.equals(item.getOwnerId(), ownerId)) {
            throw new UnknownUserException("редактировать вещь может только владелец");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        log.debug("Изменён объект вещи: {}", item);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    public ItemDto getItem(Long itemId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            log.error("вещи с id={} не существует", itemId);
            throw new UnknownItemException("попытка получить несуществующую вещь");
        }

        return itemMapper.toItemDto(itemRepository.findById(itemId).get());
    }

    public List<ItemWithBookingInfoDto> getItemsByOwner(Long ownerId) {
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(itemMapper::toItemWithBookingInfoDto)
                .collect(toList());
    }

    public List<ItemDto> searchItem(String text) {
        String query = text.toLowerCase();

        if (query.trim().length() == 0) {
            return new ArrayList<>();
        } else {
            return itemRepository.getItemsBySearchQuery(query).stream()
                    .map(itemMapper::toItemDto)
                    .collect(toList());
        }
    }
}
