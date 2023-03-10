package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto getUser(Long userId);

    List<UserDto> getAll();

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, Long userId);

    UserDto remove(Long userId);
}
