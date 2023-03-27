package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemRequestDto> create(@RequestBody ItemRequestDto itemRequestDto,
                                                 @RequestHeader(USER_ID) Long userId) {
        return ResponseEntity.ok().body(itemRequestService.create(itemRequestDto, userId, LocalDateTime.now()));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getRequestsByOwner(@RequestHeader(USER_ID) Long userId) {
        return ResponseEntity.ok().body(itemRequestService.getRequestsByOwner(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAll(@RequestHeader(USER_ID) Long userId,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(required = false) Integer size) {
        return ResponseEntity.ok().body(itemRequestService.getAll(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getItemRequest(@PathVariable("requestId") Long itemRequestId,
                                         @RequestHeader(USER_ID) Long userId) {
        return ResponseEntity.ok().body(itemRequestService.getItemRequest(itemRequestId, userId));
    }
}
