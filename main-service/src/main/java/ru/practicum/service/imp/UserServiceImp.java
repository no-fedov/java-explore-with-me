package ru.practicum.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.UserDto;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.UserService;

import java.util.List;

import static ru.practicum.dto.mapper.UserMapper.*;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = userFromUserDto(userDto);
        userRepository.save(user);
        return userDtoFromUser(user);
    }

    @Override
    public UserDto findUser(Long id) {
        return userDtoFromUser(findUserById(id));
    }

    @Override
    public List<UserDto> findUserPage(List<Long> ids, Pageable page) {
        if (ids == null) {
            return listUserDtoFroUser(userRepository.findAll(page).toList());
        }
        return listUserDtoFroUser(userRepository.findByIdIn(ids, page).toList());
    }

    @Override
    public void removeUser(Long id) {
        userRepository.deleteById(findUser(id).getId());
    }

    private User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException());
    }
}
