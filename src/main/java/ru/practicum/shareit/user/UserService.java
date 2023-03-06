package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.userExeption.ConflictUserException;
import ru.practicum.shareit.exception.userExeption.UnknownUserException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validator.UserValidator;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserDto getUser(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.error("пользователя с id={} не существует", userId);
            throw new UnknownUserException("попытка получить несуществующего пользователя");
        }

        return userMapper.toUserDto(userRepository.findById(userId).get());
    }

    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(toList());
    }

    public UserDto create(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        UserValidator.validate(user);

        try {
            return userMapper.toUserDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictUserException("пользователь с таким email уже существует");
        }
    }

    public UserDto update(UserDto userDto, Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.error("пользователя с id={} не существует", userId);
            throw new UnknownUserException("попытка обновить несуществующего пользователя");
        }

        User user = userRepository.findById(userId).get();

        if (userDto.getName() != null) {
            UserValidator.loginValidate(userMapper.toUser(userDto));
        }

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        log.debug("Изменён объект пользователя: {}", user);
        return userMapper.toUserDto(userRepository.save(user));
    }

    public UserDto remove(Long userId) {
        userRepository.deleteById(userId);
        if (userRepository.findById(userId).isEmpty()) {
            return null;
        } else {
            return userMapper.toUserDto(userRepository.findById(userId).get());
        }
    }
}
