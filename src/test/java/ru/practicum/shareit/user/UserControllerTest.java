package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.exception.*;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private UserService userServiceMock;

    @SneakyThrows
    @Test
    void getUser_UserFound_ReturnUser() {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("testUser")
                .email("test@test.com")
                .build();
        when(userServiceMock.getUser(1L)).thenReturn(user);

        mockMvc.perform(get("/users/{id}", user.getId()))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.name").value((user.getName())));
        verify(userServiceMock).getUser(user.getId());
    }

    @SneakyThrows
    @Test
    void getUser_UserNotFound_returnNotFoundException() {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("testUser")
                .email("test@test.com")
                .build();

        when(userServiceMock.getUser(1L)).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/users/{id}", user.getId())).andExpect(status().isNotFound());
        verify(userServiceMock).getUser(1L);

    }

    @SneakyThrows
    @Test
    void updateUser_UserFound_ReturnOk() {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("testUser")
                .email("test@test.com")
                .build();
        when(userServiceMock.updateUser(user)).thenReturn(user);

        mockMvc.perform(patch("/users/{id}", user.getId())
                        .content(objectMapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect((jsonPath("$.email").value(user.getEmail())));
        verify(userServiceMock).updateUser(user);

    }

    @SneakyThrows
    @Test
    void updateUser_UserIsNotValid_returnValidationException() {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("testUser")
                .email("test@test.com")
                .build();
        when(userServiceMock.updateUser(user)).thenThrow(ValidationException.class);

        mockMvc.perform(patch("/users/{id}", user.getId())
                        .content(objectMapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());

        verify(userServiceMock).updateUser(user);
    }

    @SneakyThrows
    @Test
    void getUsers() {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("testUser")
                .email("test@test.com")
                .build();
        when(userServiceMock.getUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(user))));
        verify(userServiceMock).getUsers();
    }

    @SneakyThrows
    @Test
    void addUser_UserValid_ReturnUser() {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("testUser")
                .email("test@test.com")
                .build();
        when(userServiceMock.addUser(user)).thenReturn(user);

        mockMvc.perform(post("/users")
                        .content(new ObjectMapper().writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect((jsonPath("$.email").value(user.getEmail())));
        verify(userServiceMock).addUser(user);
    }

    @SneakyThrows
    @Test
    void addUser_UserIsInvalid_ReturnValidationException() {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("testUser")
                .email("test@test.com")
                .build();
        when(userServiceMock.addUser(user)).thenThrow(ValidationException.class);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());
        verify(userServiceMock).addUser(user);
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("testUser")
                .email("test@test.com")
                .build();
        mockMvc.perform(delete("/users/{id}", user.getId()))
                .andExpect(status().isOk());
        verify(userServiceMock).deleteUser(user.getId());
    }
}
