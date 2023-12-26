package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto userDto);

    List<UserDto> getUsers();

    UserDto getUser(Long id);

    UserDto updateUser(UserDto userDto);

    void deleteUser(Long id);
}
