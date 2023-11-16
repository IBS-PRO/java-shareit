package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Отсутствует обязательное поле email");
        }
        log.info("User с id: {} создан", userDto.getId());
        return UserMapper.toUserDto(userStorage.addUser(user));
    }

    @Override
    public List<UserDto> getUsers() {
        log.info("Список всех Users получен");
        return userStorage.getUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(Long id) {
        log.info("User с id: {} получен", id);
        return UserMapper.toUserDto(userStorage.getUser(id));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        userDto.setId(userId);
        System.out.println("test2");
        User userFromDto = UserMapper.toUser(userDto);
        User userFromStorage = userStorage.getUser(userFromDto.getId());
        if (userFromDto.getName() == null) {
            userFromDto.setName(userFromStorage.getName());
        }
        if (userFromDto.getEmail() == null) {
            userFromDto.setEmail(userFromStorage.getEmail());
        }
        log.info("User с id: {} обновлен", userId);
        return UserMapper.toUserDto(userStorage.updateUser(userFromDto));
    }

    @Override
    public void deleteUser(Long id) {
        log.info("User с id: {} удален", id);
        userStorage.deleteUser(id);
    }

}
