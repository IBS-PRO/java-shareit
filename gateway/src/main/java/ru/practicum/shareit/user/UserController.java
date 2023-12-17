package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @ResponseBody
    @Validated(Create.class)
    @PostMapping
    public ResponseEntity addUser(@RequestBody @Valid UserDto userDto) {
        log.info("POST '/users'. Запрос на добавление нового пользователя с телом {} ", userDto);
        ResponseEntity response = userClient.addUser(userDto);
        log.info("POST '/users'. Ответ, новый пользователь: {}, успешно добавлен ", response);
        return response;
    }

    @GetMapping
    public ResponseEntity getUsers() {
        log.info("GET '/users'. Запрос на получение списка всех пользователей");
        ResponseEntity response = userClient.getUsers();
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity getUser(@PathVariable Long id) {
        log.info("GET '/users/{id}'. Запрос на получение пользователя с id: {}", id);
        ResponseEntity response = userClient.getUser(id);
        log.info("GET '/users/{id}'. Ответ, пользователь c id: {}, {} ", id, response);
        return response;
    }

    @ResponseBody
    @Validated(Update.class)
    @PatchMapping("/{userId}")
    public ResponseEntity updateUser(@PathVariable Long userId,
                              @RequestBody UserDto userDto) {
        log.info("PATCH '/users/{userId}'. Запрос на обновление пользователя с id {} и телом {} ", userId,userDto);
        ResponseEntity response = userClient.updateUser(userId, userDto);
        log.info("PATCH '/users/{userId}'. Ответ, пользователь с id {} обновлен: {} ", userId, response);
        return response;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("DELETE 'users/{id}'. Запрос на удаление пользователя с id {} ", id);
        userClient.deleteUser(id);
        log.info("DELETE 'users/{id}'. Ответ, пользователь с id {}, успешно удален ", id);
    }
}
