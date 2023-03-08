package ru.practicum.shareit.booking.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {

    private Long id;
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull
    @Future
    private LocalDateTime end;
    private Status status;
    private UserDto booker;
    @NotNull
    private ItemDto item;
}
