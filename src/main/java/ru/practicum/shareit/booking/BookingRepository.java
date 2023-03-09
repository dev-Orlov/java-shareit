package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    Booking getFirstByItemIdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime end);

    Booking getTopByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime start);
    
    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :id AND b.end < :currentTime AND upper(b.status) = UPPER('APPROVED')" +
            "ORDER BY b.start DESC")
    List<Booking> getByBookerIdStatePast(@Param("id") Long id, @Param("currentTime") LocalDateTime currentTime);
}
