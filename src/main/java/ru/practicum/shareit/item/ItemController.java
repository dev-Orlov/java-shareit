package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingInfoDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemServiceImpl itemService;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemDto> create(@Valid @RequestBody ItemDto itemDto, @RequestHeader(USER_ID) Long ownerId) {
        return ResponseEntity.ok().body(itemService.create(itemDto, ownerId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                          @RequestHeader(USER_ID) Long ownerId) {
        return ResponseEntity.ok().body(itemService.update(itemDto, itemId, ownerId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemWithBookingInfoDto> getItem(@PathVariable Long itemId,
                                                          @RequestHeader(USER_ID) Long userId) {
        return ResponseEntity.ok().body(itemService.getItem(itemId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemWithBookingInfoDto>> getItemsByOwner(@RequestHeader(USER_ID) Long ownerId) {
        return ResponseEntity.ok().body(itemService.getItemsByOwner(ownerId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItem(@RequestParam String text) {
        return ResponseEntity.ok().body(itemService.searchItem(text));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> createComment(@Valid @RequestBody CommentDto commentDto,
                                                    @RequestHeader(USER_ID) Long userId, @PathVariable Long itemId) {
        return ResponseEntity.ok().body(itemService.createComment(commentDto, itemId, userId));
    }
}
