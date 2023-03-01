package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class Item {

    private Long id;
    @NotNull
    @NotBlank
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private Long requestId;
}
