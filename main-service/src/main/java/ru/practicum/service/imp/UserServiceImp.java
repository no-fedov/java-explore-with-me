package ru.practicum.service.imp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.mapper.UserMapper;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.UserActionException;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.UserService;

import java.util.List;
import java.util.Optional;

import static ru.practicum.dto.mapper.UserMapper.convertToUserDtoFromUser;
import static ru.practicum.dto.mapper.UserMapper.listUserDtoFroUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto addUser(NewUserRequest newUserRequest) {
        checkEmailDuplication(newUserRequest.getEmail());
        User user = UserMapper.convertToUserFromNewUserRequest(newUserRequest);
        userRepository.save(user);
        log.info("saved User: {}", user);
        return convertToUserDtoFromUser(user);
    }

    @Override
    public UserDto findUser(Long id) {
        log.info("search user where id = {}", id);
        return convertToUserDtoFromUser(findUserById(id));
    }

    @Override
    public List<UserDto> findUserPage(List<Long> ids, Pageable page) {
        log.info("search user where id in ({})", ids);
        if (ids == null) {
            return listUserDtoFroUser(userRepository.findAll(page).toList());
        }
        return listUserDtoFroUser(userRepository.findByIdIn(ids, page).toList());
    }

    @Override
    public void removeUser(Long id) {
        log.info("delete user where id = {}", id);
        userRepository.deleteById(findUser(id).getId());
    }

    private User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("пользователь с id = "
                + id + "не найден"));
    }

    private void checkEmailDuplication(String email) {
        Optional<User> categoryByName = userRepository.findByEmail(email);
        if (categoryByName.isPresent()) {
            throw new UserActionException("Дублирование имени категории");
        }
    }
}
