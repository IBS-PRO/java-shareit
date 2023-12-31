package ru.practicum.shareit.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    default User checkUser(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Не найден пользователь с ид: " + id));
    }
}
