package ru.practicum.controller.adminapi;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.service.UserService;

import java.util.List;

import static ru.practicum.controller.PageConstructor.getPage;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@Slf4j
@Validated
public class UserAdminController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto postUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("POST: /admin/users {}", newUserRequest);
        return userService.addUser(newUserRequest);
    }

    @GetMapping
    public List<UserDto> findUsersPage(@RequestParam(required = false) List<Long> ids,
                                       @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                       @Positive @RequestParam(defaultValue = "10") Integer size) {
        Pageable page = getPage(from, size);
        return userService.findUserPage(ids, page);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.removeUser(id);
    }
}
