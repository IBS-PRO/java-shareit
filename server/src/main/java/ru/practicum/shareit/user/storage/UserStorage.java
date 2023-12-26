package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user);

    List<User> getUsers();

    User getUser(Long id);

    User updateUser(User user);

    void deleteUser(Long id);

    boolean isEmailInvalid(Long id, String email);
}
