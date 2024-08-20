package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto findUser(Long id);

    List<UserDto> findUserPage(List<Long> ids, Pageable page);

    void removeUser(Long id);
}
