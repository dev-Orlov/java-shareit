package ru.practicum.shareit.booking.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CreatedBookingDto {

    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
    private Long bookerId;
    private Long itemId;
}
