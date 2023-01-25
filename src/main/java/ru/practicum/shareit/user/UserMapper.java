package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;

@Component
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User model);

    User toUser(UserDto dto);
}
