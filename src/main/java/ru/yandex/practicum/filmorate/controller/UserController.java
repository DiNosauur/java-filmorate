package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private Long sequenceId = 1l;
    private final Map<Long, User> users = new HashMap<>();

    private List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        for (User user : users.values()) {
            userList.add(user);
        }
        return userList;
    }

    @GetMapping
    public Collection<User> findAll() {
        return getAllUsers();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        if(user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Адрес электронной почты не может быть пустым.");
        } else if(user.getEmail().indexOf("@") == -1) {
            throw new ValidationException("Адрес электронной почты должен содержать символ @.");
        }
        if(user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        }
        if(user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("День рождения не может быть больше текущей даты.");
        }
        if(user.getId() == null) {
            user.setId(sequenceId++);
        } else if(user.getId() < 0) {
            throw new ValidationException("Идентификатор пользователя не может быть отрицательным.");
        }
        if(users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с таким идентификатором " +
                    user.getId() + " уже зарегистрирован.");
        }
        users.put(user.getId(), user);
        log.info("Добавлен юзер {}", user.getEmail());
        return user;
    }

    @PutMapping
    public User put(@RequestBody User user) {
        if(user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Адрес электронной почты не может быть пустым.");
        }
        if(user.getId() == null || user.getId() < 0) {
            throw new ValidationException("Идентификатор пользователя не может быть пустым или отрицательным");
        }
        users.put(user.getId(), user);
        log.info("Обновлён юзер c id = {}", user.getId());
        return user;
    }
}
