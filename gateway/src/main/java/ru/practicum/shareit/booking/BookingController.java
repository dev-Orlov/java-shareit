package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
	private final BookingClient bookingClient;
	private static final String BOOKER = "X-Sharer-User-Id";

	@PostMapping
	public ResponseEntity<Object> create(@RequestHeader(BOOKER) Long userId,
										 @RequestBody @Valid BookItemRequestDto requestDto) {
		return bookingClient.create(userId, requestDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> update(@PathVariable Long bookingId,
											 @RequestHeader(BOOKER) Long userId, @RequestParam Boolean approved) {
		return bookingClient.update(bookingId, userId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader(BOOKER) long userId,
											 @PathVariable Long bookingId) {
		return bookingClient.getBooking(bookingId, userId);
	}

	@GetMapping
	public ResponseEntity<Object> getUserBookings(@RequestHeader(BOOKER) Long userId,
												  @RequestParam(name = "state", defaultValue = "all") String stateParam,
												  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
											  Integer from,
												  @RequestParam(required = false) Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		return bookingClient.getUserBookings(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getOwnerBookings(@RequestParam(name = "state", defaultValue = "all")
												   String stateParam,
												   @RequestHeader(BOOKER) Long userId,
												   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
												   @RequestParam(required = false) Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		return bookingClient.getOwnerBookings(userId, state, from, size);
	}
}
