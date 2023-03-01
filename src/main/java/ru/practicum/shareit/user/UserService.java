package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final UserMapper userMapper;

    public UserDto getUser(Long userId) {
        return userMapper.toUserDto(userStorage.getUser(userId));
    }

    public List<UserDto> getAll() {
        return userStorage.getAll().stream()
                .map(userMapper::toUserDto)
                .collect(toList());
    }

    public UserDto create(UserDto userDto) {
        return userMapper.toUserDto(userStorage.create(userMapper.toUser(userDto)));
    }

    public UserDto update(UserDto userDto, Long userId) {
        return userMapper.toUserDto(userStorage.update(userMapper.toUser(userDto), userId));
    }

    public UserDto remove(Long userId) {
        return userMapper.toUserDto(userStorage.remove(userId));
    }
}
