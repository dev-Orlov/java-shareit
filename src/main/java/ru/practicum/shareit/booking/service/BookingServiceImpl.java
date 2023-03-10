package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.bookingExeption.IncorrectBookingException;
import ru.practicum.shareit.exception.bookingExeption.UnknownBookingException;
import ru.practicum.shareit.validator.Validator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
    private final Validator validator;

    @Override
    public BookingDto create(CreatedBookingDto createdBookingDto, Long userId) {
        validator.checkUserExistById(userId);
        validator.checkItemExistById(createdBookingDto.getItemId());
        validator.checkItemAvailable(createdBookingDto.getItemId());

        for (Booking booking : bookingRepository.findAll()) {
            if (booking.getItem().getId().equals(createdBookingDto.getItemId()) &&
                    booking.getBooker().getId().equals(createdBookingDto.getBookerId())) {
                log.error("бронирование уже было создано");
                throw new IncorrectBookingException("попытка повторно создать бронирование");
            }
        }

        Booking booking = bookingMapper.toBooking(createdBookingDto, userId);
        validator.checkBookingTime(createdBookingDto);

        if (booking.getItem().getOwnerId().equals(userId)) {
            log.error("нельзя забронировать собственную вещь");
            throw new UnknownBookingException("попытка забронировать свою вещь");
        }
        log.debug("Создано бронирование: {}", booking);

        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto update(Long bookingId, Long bookerId, Boolean approved) {
        validator.checkUserExistById(bookerId);

        if (bookingRepository.findById(bookingId).isEmpty()) {
            log.error("бронирования с id={} не существует", bookingId);
            throw new UnknownBookingException("попытка изменить статус несуществующего бронирования");
        }
        Booking booking = bookingRepository.findById(bookingId).get();

        if (!booking.getItem().getOwnerId().equals(bookerId)) {
            log.error("подтверждение запроса на бронирование может быть выполнено только владельцем вещи");
            throw new UnknownBookingException("попытка подтвердить чужое бронирование");
        }

        if (booking.getStatus().equals(Status.WAITING)) {
            if (approved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
        } else if (booking.getStatus().equals(Status.APPROVED)) {
            if (!approved) {
                booking.setStatus(Status.CANCELED);
            } else {
                log.error("подтверждение запроса на бронирование уже было выполнено");
                throw new IncorrectBookingException("попытка повторно подтвердить бронирование");
            }
        }
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(Long bookingId, Long userId) {
        validator.checkUserExistById(userId);

        if (bookingRepository.findById(bookingId).isEmpty()) {
            log.error("бронирования с id={} не существует", bookingId);
            throw new UnknownBookingException("попытка получить несуществующее бронирование");
        }

        Long ownerId = bookingRepository.findById(bookingId).get().getItem().getOwnerId();
        Long bookerId = bookingRepository.findById(bookingId).get().getBooker().getId();
        if (ownerId.equals(userId) || bookerId.equals(userId)) {
            return bookingMapper.toBookingDto(bookingRepository.findById(bookingId).get());
        } else {
            log.error("данные о бронировании может получить только автор бронирования или владелец вещи");
            throw new UnknownBookingException("попытка получить чужое бронирование");
        }
    }

    @Override
    public List<BookingDto> getUserBookings(String state, Long userId) {
        validator.checkUserExistById(userId);

        List<Booking> bookings;
        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByBookerId(userId, sortByStartDesc);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), sortByStartDesc);
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), sortByStartDesc);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(),
                        sortByStartDesc);
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatus(userId, Status.WAITING, sortByStartDesc);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatus(userId, Status.REJECTED, sortByStartDesc);
                break;
            default:
                log.error("некорректное условие бронирования state={} ", state);
                throw new IncorrectBookingException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnerBookings(String state, Long userId) {
        validator.checkUserExistById(userId);

        List<Booking> bookings;
        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByItem_OwnerId(userId, sortByStartDesc);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByItem_OwnerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), sortByStartDesc);
                break;
            case "PAST":
                bookings = bookingRepository.findByItem_OwnerIdAndEndIsBefore(userId, LocalDateTime.now(), sortByStartDesc);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByItem_OwnerIdAndStartIsAfter(userId, LocalDateTime.now(),
                        sortByStartDesc);
                break;
            case "WAITING":
                bookings = bookingRepository.findByItem_OwnerIdAndStatus(userId, Status.WAITING, sortByStartDesc);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByItem_OwnerIdAndStatus(userId, Status.REJECTED, sortByStartDesc);
                break;
            default:
                log.error("некорректное условие бронирования state={} ", state);
                throw new IncorrectBookingException("Unknown state: " + state);
        }
        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
