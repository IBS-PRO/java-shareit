package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RequestServiceImplTest {

    UserDto userDto;
    UserDto createdUser;
    ItemRequestDto requestDtoFromController;
    ItemResponseDto createdRequestReturnDto;

    @Autowired
    ItemRequestService requestService;
    @Autowired
    private UserService userService;

    @BeforeEach
    void beforeEach() {
        userDto = new UserDto();
        userDto.setName("testName");
        userDto.setEmail("testEmail@mail.com");
        createdUser = userService.addUser(userDto);

        requestDtoFromController = new ItemRequestDto();
        requestDtoFromController.setDescription("testDescription");
        requestDtoFromController.setRequestId(createdUser.getId());
        createdRequestReturnDto = requestService.addItemRequest(requestDtoFromController, createdUser.getId());

    }

    @Test
    void getItemRequest() {
        ItemResponseDto actual = requestService.getItemRequest(createdRequestReturnDto.getId(), createdUser.getId());

        assertNotNull(actual);
        assertEquals(createdRequestReturnDto.getId(), actual.getId());
        assertEquals(createdRequestReturnDto.getDescription(), actual.getDescription());
        assertEquals(createdRequestReturnDto.getCreated().withNano(0), actual.getCreated().withNano(0));
    }

    @Test
    void getItemRequest_isUserInvalid_Exception() {
        assertThrows(NotFoundException.class, () -> requestService.getItemRequest(createdRequestReturnDto.getId(), 999L));
    }

    @Test
    void getRequestorRequest() {
        List<ItemResponseDto> actualList = requestService.getRequestorRequest(createdUser.getId());
        assertFalse(actualList.isEmpty());
        assertEquals(actualList.get(0).getRequestId(), createdUser.getId());
    }

    @Test
    void getItemRequests() {
        UserDto userDto1 = new UserDto();
        userDto1.setName("testName2");
        userDto1.setEmail("testMail@mail.com");
        UserDto actualUser = userService.addUser(userDto1);
        List<ItemResponseDto> actualList = requestService.getItemRequests(actualUser.getId(), 0, 99);
        assertFalse(actualList.isEmpty());
        userService.deleteUser(actualUser.getId());
    }

    @AfterEach
    void afterEach() {
        requestService.deleteItemRequest(createdRequestReturnDto.getId(), createdUser.getId());
        userService.deleteUser(createdUser.getId());
    }

}
