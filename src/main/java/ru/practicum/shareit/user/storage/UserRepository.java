package ru.practicum.shareit.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    default User checkUser(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Не найден пользователь с ид: " + id));
    }
}
