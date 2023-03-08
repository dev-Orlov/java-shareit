package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerId(Long userId, Sort sort);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime start,
                                                              LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndEndIsBefore(Long userId, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(Long userId, LocalDateTime start, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long userId, Status status, Sort sort);

    List<Booking> findByItem_OwnerId(Long ownerId, Sort sort);

    List<Booking> findByItem_OwnerIdAndStartIsBeforeAndEndIsAfter(Long ownerId, LocalDateTime start,
                                                                   LocalDateTime end, Sort sort);

    List<Booking> findByItem_OwnerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByItem_OwnerIdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByItem_OwnerIdAndStatus(Long bookerId, Status status, Sort sort);

    Booking findFirstByItem_IdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime end);

    Booking findFirstByItem_IdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime end);

    Booking findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(Long itemId, Long userId,
                                                                  LocalDateTime end, Status status);
}
