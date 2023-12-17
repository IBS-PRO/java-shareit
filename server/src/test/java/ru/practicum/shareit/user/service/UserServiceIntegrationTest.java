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
    void getUsers() {
        List<UserDto> list = userService.getUsers();
        assertFalse(list.isEmpty());
    }

    @AfterEach
    void deleteUser() {
        userService.deleteUser(createdUserFromRepo.getId());
    }
}
