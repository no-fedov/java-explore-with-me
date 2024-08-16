package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.UserDto;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;

import static ru.practicum.dto.mapper.UserMapper.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto addUser(UserDto userDto) {
        User user = userFromUserDto(userDto);
        userRepository.save(user);
        return userDtoFromUser(user);
    }

    public List<UserDto> findUserPage(List<Long> ids, Pageable page) {
        if (ids == null) {
            return listUserDtoFroUser(userRepository.findAll(page).toList());
        }
        return listUserDtoFroUser(userRepository.findByIdIn(ids, page).toList());
    }

    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException());
    }

    public void removeUser(Long id) {
        userRepository.deleteById(findUser(id).getId());
    }
}
