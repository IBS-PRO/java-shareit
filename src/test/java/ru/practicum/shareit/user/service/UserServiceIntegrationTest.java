package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceIntegrationTest {

    UserDto userDtoFromController;
    UserDto createdUserFromRepo;

    @Autowired
    private UserService userService;

    @BeforeEach
    void beforeEach() {
        userDtoFromController = new UserDto();
        userDtoFromController.setName("testName");
        userDtoFromController.setEmail("testEmail@mail.com");
        createdUserFromRepo = userService.addUser(userDtoFromController);
    }

    @Test
    void getUser() {
        UserDto actual = userService.getUser(createdUserFromRepo.getId());
        assertEquals(actual, createdUserFromRepo);
    }

    @Test
    void getUser_userIdIsInvalid_throwException() {
        assertThrows(NotFoundException.class, () -> userService.getUser(999L));
    }

    @Test
    void addUser() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("testUser")
                .email("test@test.com")
                .build();
        UserDto created = userService.addUser(userDto);
        assertEquals(userDto.getName(), created.getName());
        assertEquals(userDto.getEmail(), created.getEmail());
    }

    @Test
    void updateUser() {
        userDtoFromController.setName("updatedName");
        userDtoFromController.setEmail("updated@Email.com");
        userDtoFromController.setId(createdUserFromRepo.getId());
        UserDto actual = userService.updateUser(userDtoFromController);
        assertEquals("updatedName", actual.getName());
        assertEquals("updated@Email.com", actual.getEmail());

    }

    @Test
    void updateUser_UserIdIsInvalid_throwException() {
        userDtoFromController.setName("updatedName");
        userDtoFromController.setEmail("updated@Email.com");
        userDtoFromController.setId(999L);
        assertThrows(NotFoundException.class, () -> userService.updateUser(userDtoFromController));
    }

    @Test
    void getUsers() {
        List<UserDto> list = userService.getUsers();
        assertFalse(list.isEmpty());
    }

    @AfterEach
    void deleteUser() {
        userService.deleteUser(createdUserFromRepo.getId());
    }
}
