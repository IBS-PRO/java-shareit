package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto addUser(@RequestBody @Valid UserDto userDto) {
        log.info("POST '/users'. Запрос на добавление нового пользователя с телом {} ", userDto);
        UserDto response = userService.addUser(userDto);
        log.info("POST '/users'. Ответ, новый пользователь: {}, успешно добавлен ", response);
        return response;
    }

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("GET '/users'. Запрос на получение списка всех пользователей");
        List<UserDto> response = userService.getUsers();
        log.info("GET '/users'. Ответ, в хранилище {} пользователь(ей): {} ", response.size(), response);
        return response;
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        log.info("GET '/users/{id}'. Запрос на получение пользователя с id: {}", id);
        UserDto response = userService.getUser(id);
        log.info("GET '/users/{id}'. Ответ, пользователь c id: {}, {} ", id, response);
        return response;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId,
                              @RequestBody UserDto userDto) {
        log.info("PATCH '/users/{userId}'. Запрос на обновление пользователя с id {} и телом {} ", userId,userDto);
        userDto.setId(userId);
        UserDto response = userService.updateUser(userDto);
        log.info("PATCH '/users/{userId}'. Ответ, пользователь с id {} обновлен: {} ", userId, response);
        return response;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("DELETE 'users/{id}'. Запрос на удаление пользователя с id {} ", id);
        userService.deleteUser(id);
        log.info("DELETE 'users/{id}'. Ответ, пользователь с id {}, успешно удален ", id);
    }

}