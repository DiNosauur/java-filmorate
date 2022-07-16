package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User createUser(User user) {
        validate(user);
        if (user.getId() != null && user.getId() < 0) {
            throw new ValidationException("Идентификатор пользователя не может быть отрицательным.");
        }
        if (user.getId() != null && getUser(user.getId()) != null) {
            throw new ValidationException("Пользователь с таким идентификатором " +
                    user.getId() + " уже зарегистрирован.");
        }
        log.debug("Сохранение данных пользователя {}", user.getName());
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        validate(user);
        if (user.getId() == null) {
            throw new ValidationException("Идентификатор пользователя не может быть пустым");
        }
        if (getUser(user.getId()) == null) {
            throw new NotFoundException(String.format(
                    "Пользователь (id = %s) не найден",
                    user.getId()));
        }
        log.debug("Изменение данных пользователя {}", user.getName());
        return userStorage.updateUser(user);
    }

    public User getUser(Long id) {
        User user = userStorage.getUser(id);
        if (user == null) {
            throw new NotFoundException(String.format(
                    "Пользователь (id = %s) не найден",
                    id));
        }
        return user;
    }

    private void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Адрес электронной почты не может быть пустым.");
        } else if (user.getEmail().indexOf("@") == -1) {
            throw new ValidationException("Адрес электронной почты должен содержать символ @.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("День рождения не может быть больше текущей даты.");
        }
    }

    public void addFriend(Long id, Long friendId) {
        if (userStorage.getUser(id) == null) {
            throw new NotFoundException(String.format(
                    "Пользователь (id = %s) не найден",
                    id));
        }
        if (userStorage.getUser(friendId) == null) {
            throw new NotFoundException(String.format(
                    "Пользователь (id = %s) не найден",
                    friendId));
        }
        userStorage.addFriend(id, friendId);
        log.debug("Пользователь (id = {}) добавил пользователя (id = {}) в друзья", id, friendId);
    }

    public void delFriend(Long id, Long friendId) {
        if (userStorage.getUser(id) == null) {
            throw new NotFoundException(String.format(
                    "Пользователь (id = %s) не найден",
                    id));
        }
        if (userStorage.getUser(friendId) == null) {
            throw new NotFoundException(String.format(
                    "Пользователь (id = %s) не найден",
                    friendId));
        }
        userStorage.delFriend(id, friendId);
        log.debug("Пользователь (id = {}) удалил пользователя (id = {}) из друзей", id, friendId);
    }

    public Collection<User> getFriends(Long id) {
        Set<User> users = new HashSet<>();
        for (Long userId : userStorage.getFriends(id)) {
            users.add(userStorage.getUser(userId));
        }
        return users;
    }

    public Collection<User> getCommonFriends(Long id, Long otherId){
        Set<Long> friends = new HashSet<>();
        Set<User> users = new HashSet<>();
        friends.addAll(userStorage.getFriends(id));
        friends.retainAll(userStorage.getFriends(otherId));
        for (Long userId : friends) {
            users.add(userStorage.getUser(userId));
        }
        return users;
    }
}
