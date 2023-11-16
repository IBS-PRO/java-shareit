package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailDuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Component
public class UserInMemoryStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private Long idCounter = 0L;

    @Override
    public User addUser(User user) {
        if (emails.contains(user.getEmail())) {
            throw new EmailDuplicateException("Ошибка такой email уже есть");
        }
        idGenerator();
        user.setId(idCounter);
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(Long id) {
        if (users.get(id) == null) {
            throw new NotFoundException("Ошибка пользователь с id: " + id + " не найден");
        }
        return users.get(id);
    }

    @Override
    public User updateUser(User user) {
        if (isEmailInvalid(user.getId(), user.getEmail())) {
            throw new EmailDuplicateException("Ошибка такой email уже используется");
        }
        User fromMap = users.get(user.getId());
        emails.remove(fromMap.getEmail());
        emails.add(user.getEmail());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        emails.remove(getUser(id).getEmail());
        users.remove(id);
    }

    @Override
    public boolean isEmailInvalid(Long id, String email) {
        if (emails.contains(email) && !users.get(id).getEmail().equals(email)) {
            throw new EmailDuplicateException("Ошибка такой email уже используется");
        }
        return false;
    }

    private void idGenerator() {
        ++idCounter;
    }

}
