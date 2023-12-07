package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

        @Mock
        private UserRepository userRepository;

        @InjectMocks
        private UserServiceImpl userService;

        @Test
        void getAll() {

                User firstUser = User.builder()
                        .id(1L)
                        .name("testUser")
                        .email("firstUser@mail.com")
                        .build();

                User secondUser = User.builder()
                        .id(1L)
                        .name("test2User")
                        .email("secondUser@mail.com")
                        .build();

                List<User> users = new ArrayList<>();
                users.add(firstUser);
                users.add(secondUser);

                when(userRepository.findAll()).thenReturn(users);

                List<UserDto> usersAfter = userService.getUsers();

                assertThat(usersAfter.size()).isEqualTo(2);
        }
}
