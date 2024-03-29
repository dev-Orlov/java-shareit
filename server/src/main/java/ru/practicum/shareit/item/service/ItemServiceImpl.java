package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.itemExeption.UnknownItemException;
import ru.practicum.shareit.exception.userExeption.UnknownUserException;
import ru.practicum.shareit.exception.userExeption.UserValidationException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingInfoDto;
import ru.practicum.shareit.item.mapper.ItemMapperWithBookingInfo;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Pagination;
import ru.practicum.shareit.util.Validator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

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
                .getTopByItemIdAndStartBeforeOrderByStartDesc(item.getId(), LocalDateTime.now()).orElse(null);
        if (lastBooking != null && !lastBooking.getStatus().equals(Status.APPROVED)) {
            lastBooking = null;
        }
        Booking nextBooking = bookingRepository
                .getTopByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now()).orElse(null);
        if (nextBooking != null && !nextBooking.getStatus().equals(Status.APPROVED)) {
            nextBooking = null;
        }
        List<CommentDto> comments = new ArrayList<>();
        for (Comment comment : getCommentsByItemId(item.getId())) {
            CommentDto commentDto = commentMapper.toCommentDto(comment);
            comments.add(commentDto);
        }

        if (item.getOwnerId().equals(userId)) {
            return itemMapperWithBookingInfo.toItemWithBookingInfoDto(item, lastBooking, nextBooking, comments);
        } else {
            return itemMapperWithBookingInfo.toItemWithBookingInfoDto(item, null, null, comments);
        }
    }

    @Override
    public List<ItemWithBookingInfoDto> getItemsByOwner(Long ownerId, Integer from, Integer size) {
        List<ItemWithBookingInfoDto> listItemExtDto = new ArrayList<>();
        Pageable pageable;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Page<Item> page;
        Pagination pager = new Pagination(from, size);

        if (size == null) {
            pageable =
                    PageRequest.of(pager.getIndex(), pager.getPageSize(), sort);
            do {
                page = itemRepository.findByOwnerId(ownerId, pageable);
                listItemExtDto.addAll(page.stream().map(item -> {
                            Booking lastBooking = bookingRepository
                                    .getTopByItemIdAndStartBeforeOrderByStartDesc(item.getId(),
                                            LocalDateTime.now()).orElse(null);
                            if (lastBooking != null && !lastBooking.getStatus().equals(Status.APPROVED)) {
                                lastBooking = null;
                            }
                            Booking nextBooking = bookingRepository
                                    .getTopByItemIdAndStartAfterOrderByStartAsc(item.getId(),
                                            LocalDateTime.now()).orElse(null);
                            if (nextBooking != null && !nextBooking.getStatus().equals(Status.APPROVED)) {
                                nextBooking = null;
                            }
                            List<CommentDto> comments = new ArrayList<>();
                            for (Comment comment : getCommentsByItemId(item.getId())) {
                                CommentDto commentDto = commentMapper.toCommentDto(comment);
                                comments.add(commentDto);
                            }

                            return itemMapperWithBookingInfo.toItemWithBookingInfoDto(item, lastBooking,
                                    nextBooking, comments);
                        }
                ).collect(toList()));
                pageable = pageable.next();
            } while (page.hasNext());

        } else {
            for (int i = pager.getIndex(); i < pager.getTotalPages(); i++) {
                pageable =
                        PageRequest.of(i, pager.getPageSize(), sort);
                page = itemRepository.findByOwnerId(ownerId, pageable);
                listItemExtDto.addAll(page.stream().map(item -> {
                            Booking lastBooking = bookingRepository
                                    .getTopByItemIdAndStartBeforeOrderByStartDesc(item.getId(),
                                            LocalDateTime.now()).orElse(null);
                            if (lastBooking != null && !lastBooking.getStatus().equals(Status.APPROVED)) {
                                lastBooking = null;
                            }
                            Booking nextBooking = bookingRepository
                                    .getTopByItemIdAndStartAfterOrderByStartAsc(item.getId(),
                                            LocalDateTime.now()).orElse(null);
                            if (nextBooking != null && !nextBooking.getStatus().equals(Status.APPROVED)) {
                                nextBooking = null;
                            }
                            List<CommentDto> comments = new ArrayList<>();
                            for (Comment comment : getCommentsByItemId(item.getId())) {
                                CommentDto commentDto = commentMapper.toCommentDto(comment);
                                comments.add(commentDto);
                            }

                            return itemMapperWithBookingInfo.toItemWithBookingInfoDto(item, lastBooking,
                                    nextBooking, comments);
                        }
                ).collect(toList()));
                if (!page.hasNext()) {
                    break;
                }
            }
            listItemExtDto = listItemExtDto.stream().limit(size).collect(toList());
        }
        return listItemExtDto;
    }

    @Override
    public List<ItemDto> searchItem(String text, Integer from, Integer size) {

        List<ItemDto> itemDtoList = new ArrayList<>();
        if ((text != null) && (!text.isEmpty()) && (!text.isBlank())) {
            text = text.toLowerCase();
            Pageable pageable;
            Sort sort = Sort.by(Sort.Direction.ASC, "name");
            Page<Item> page;
            Pagination pager = new Pagination(from, size);

            if (size == null) {
                pageable =
                        PageRequest.of(pager.getIndex(), pager.getPageSize(), sort);
                do {
                    page = itemRepository.getItemsBySearchQuery(text, pageable);
                    itemDtoList.addAll(page.stream().map(itemMapper::toItemDto).collect(toList()));
                    pageable = pageable.next();
                } while (page.hasNext());

            } else {
                for (int i = pager.getIndex(); i < pager.getTotalPages(); i++) {
                    pageable =
                            PageRequest.of(i, pager.getPageSize(), sort);
                    page = itemRepository.getItemsBySearchQuery(text, pageable);
                    itemDtoList.addAll(page.stream().map(itemMapper::toItemDto).collect(toList()));
                    if (!page.hasNext()) {
                        break;
                    }
                }
                itemDtoList = itemDtoList.stream().limit(size).collect(toList());
            }
        }
        return itemDtoList;
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        validator.checkItemExistById(itemId);
        validator.checkUserExistById(userId);

        User user = userRepository.findById(userId).get();
        Item item = itemRepository.findById(itemId).get();
        Comment comment = commentMapper.toComment(commentDto, user, item);

        List<Booking> booking = bookingRepository.getByBookerIdStatePast(comment.getAuthor().getId(),
                LocalDateTime.now());
        if (booking.isEmpty()) {
            log.error("пользователь ещё не бронировал вещи");
            throw new UserValidationException("комментарий можно оставить только после завершения брони");
        }
        commentRepository.save(comment);
        return commentMapper.toCommentDto(comment);
    }

    @Override
    public List<Comment> getCommentsByItemId(Long itemId) {
        return commentRepository.getByItem_IdOrderByCreatedDesc(itemId);
    }

    @Override
    public List<ItemDto> getItemsByRequest(Long requestId) {
        return itemRepository.findAllByRequestId(requestId,
                        Sort.by(Sort.Direction.DESC, "id")).stream().map(itemMapper::toItemDto)
                .collect(toList());
    }
}
