package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.itemExeption.UnknownItemException;
import ru.practicum.shareit.exception.userExeption.UnknownUserException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingInfoDto;
import ru.practicum.shareit.item.mapper.ItemMapperWithBookingInfo;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.validator.Validator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;
    private final ItemMapperWithBookingInfo itemMapperWithBookingInfo;
    private final ItemRepository itemRepository;
    private final Validator validator;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        Item item = itemMapper.toItem(itemDto, ownerId);
        validator.checkUserExist(item);

        log.debug("Создан объект вещи: {}", item);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
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

    @Override
    public ItemWithBookingInfoDto getItem(Long itemId, Long userId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            log.error("вещи с id={} не существует", itemId);
            throw new UnknownItemException("попытка получить несуществующую вещь");
        }
        Item item = itemRepository.findById(itemId).get();
        Booking lastBooking = bookingRepository
                .findFirstByItem_IdAndEndBeforeOrderByEndDesc(item.getId(), LocalDateTime.now());
        Booking nextBooking = bookingRepository
                .findFirstByItem_IdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now());
        if (item.getOwnerId().equals(userId)) {
            return itemMapperWithBookingInfo.toItemWithBookingInfoDto(item, lastBooking, nextBooking);
        } else {
            return itemMapperWithBookingInfo.toItemWithBookingInfoDto(item, null, null);
        }
    }

    @Override
    public List<ItemWithBookingInfoDto> getItemsByOwner(Long ownerId) {
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(item -> {
                            //List<Comment> comments = getCommentsByItemId(item);
                            Booking lastBooking = bookingRepository
                                    .findFirstByItem_IdAndEndBeforeOrderByEndDesc(item.getId(), LocalDateTime.now());
                            Booking nextBooking = bookingRepository
                                    .findFirstByItem_IdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now());
                            return itemMapperWithBookingInfo.toItemWithBookingInfoDto(item, lastBooking,
                                    nextBooking);
                        }
                )
                .sorted(Comparator.comparing(ItemWithBookingInfoDto::getId))
                .collect(toList());
    }

    @Override
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
