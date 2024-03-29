package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private static final String BOOKER = "X-Sharer-User-Id";
    private final BookingService bookingService;


    @PostMapping
    public ResponseEntity<BookingDto> create(@RequestBody CreatedBookingDto createdBookingDto,
                                             @RequestHeader(BOOKER) Long bookerId) {
        return ResponseEntity.ok().body(bookingService.create(createdBookingDto, bookerId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> update(@PathVariable Long bookingId,
                             @RequestHeader(BOOKER) Long userId, @RequestParam Boolean approved) {
        return ResponseEntity.ok().body(bookingService.update(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBooking(@PathVariable Long bookingId, @RequestHeader(BOOKER) Long userId) {
        return ResponseEntity.ok().body(bookingService.getBooking(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getUserBookings(@RequestParam(name = "state",
            defaultValue = "ALL") String state, @RequestHeader(BOOKER) Long userId,
                                                            @RequestParam(defaultValue = "0") Integer from,
                                                            @RequestParam(required = false) Integer size) {
        return ResponseEntity.ok().body(bookingService.getUserBookings(state, userId, from, size));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getOwnerBookings(@RequestParam(name = "state",
            defaultValue = "ALL") String state, @RequestHeader(BOOKER) Long userId,
                                                             @RequestParam(defaultValue = "0") Integer from,
                                                             @RequestParam(required = false) Integer size) {
        return ResponseEntity.ok().body(bookingService.getOwnerBookings(state, userId, from, size));
    }
}
