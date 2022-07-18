package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private Long sequenceId = 1l;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User createUser(User user) {
        if (user.getId() == null) {
            user.setId(sequenceId++);
        } else if (user.getId() < 0) {
            throw new ValidationException("Идентификатор пользователя не может быть отрицательным.");
        }
        if (users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с таким идентификатором " +
                    user.getId() + " уже зарегистрирован.");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new ValidationException("Идентификатор пользователя не может быть пустым");
        }
        if (users.get(user.getId()) == null) {
            throw new NotFoundException(String.format(
                    "Пользователь (id = %s) не найден",
                    user.getId()));
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(Long id) {
        if (id == null) {
            return null;
        }
        if (users.get(id) == null) {
            throw new NotFoundException(String.format(
                    "Пользователь (id = %s) не найден",
                    id));
        }
        return users.get(id);
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        if (id != null && friendId != null) {
            users.get(id).getFriends().add(friendId);
            users.get(friendId).getFriends().add(id);
        }
    }

    @Override
    public void delFriend(Long id, Long friendId) {
        if (id != null && friendId != null) {
            users.get(id).getFriends().remove(friendId);
            users.get(friendId).getFriends().remove(id);
        }
    }

    @Override
    public Collection<Long> getFriends(Long id) {
        if (id == null) {
            return null;
        }
        if (users.get(id) == null) {
            throw new NotFoundException(String.format(
                    "Пользователь (id = %s) не найден",
                    id));
        }
        return users.get(id).getFriends();
    }
}
