package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.requestException.UnknownRequestException;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.util.Pagination;
import ru.practicum.shareit.util.Validator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final Validator validator;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId, LocalDateTime created) {
        validator.checkUserExistById(userId);

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto, userId, created);
        log.debug("Создан объект запроса: {}", itemRequest);
        return itemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getRequestsByOwner(Long userId) {
        validator.checkUserExistById(userId);

        List<ItemRequest> requestList = itemRequestRepository.findAllByUserId(userId, Sort.by(Sort.Direction.DESC,
                "created"));

        if (requestList.size() > 0) {
            return requestList.stream().map(itemRequestMapper::toItemRequestDto).collect(toList());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, Integer from, Integer size) {
        validator.checkUserExistById(userId);

        List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();
        Pageable pageable;
        Page<ItemRequest> page;
        Pagination pager = new Pagination(from, size);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");

        if (size == null) {
            List<ItemRequest> itemRequestList = itemRequestRepository.findAllByUserIdNotOrderByCreatedDesc(userId);
            itemRequestDtoList.addAll(itemRequestList.stream().skip(from).map(itemRequestMapper::toItemRequestDto)
                    .collect(toList()));
        } else {
            for (int i = pager.getIndex(); i < pager.getTotalPages(); i++) {
                pageable = PageRequest.of(i, pager.getPageSize(), sort);
                page = itemRequestRepository.findAllByUserIdNot(userId, pageable);
                itemRequestDtoList.addAll(page.stream().map(itemRequestMapper::toItemRequestDto).collect(toList()));
                if (!page.hasNext()) {
                    break;
                }
            }
            itemRequestDtoList = itemRequestDtoList.stream().limit(size).collect(toList());
        }
        return itemRequestDtoList;
    }

    @Override
    public ItemRequestDto getItemRequest(Long itemRequestId, Long userId) {
        validator.checkUserExistById(userId);

        if (itemRequestRepository.findById(itemRequestId).isEmpty()) {
            log.error("запроса с id={} не существует", itemRequestId);
            throw new UnknownRequestException("попытка получить несуществующий запрос");
        }

        return itemRequestMapper.toItemRequestDto(itemRequestRepository.findById(itemRequestId).get());
    }
}
