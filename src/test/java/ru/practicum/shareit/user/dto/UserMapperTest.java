package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    @Test
    void toUser_FromUserDto() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("testUser")
                .email("test@test.com")
                .build();
        User user = UserMapper.toUser(userDto);

        assertNotNull(user);

        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    void toUserDto_FromUser() {
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@test.com")
                .build();
        UserDto userDto = UserMapper.toUserDto(user);

        assertNotNull(userDto);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }
}
