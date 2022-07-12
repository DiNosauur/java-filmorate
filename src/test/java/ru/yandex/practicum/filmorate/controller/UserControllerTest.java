package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController uc;

    @BeforeEach
    public void BeforeEach() {
        uc = new UserController(new UserService(new InMemoryUserStorage()));
    }

    @Test
    void createTest() {
        uc.create(User.builder()
                .email("email@mail.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(2022, 1, 1))
                .build());
        Collection<User> users = uc.findAll();
        assertEquals(1, users.size(), "Неверное количество пользователей.");
    }

    @Test
    void emailValidTest() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> uc.create(User.builder()
                        .email("")
                        .login("login")
                        .name("name")
                        .birthday(LocalDate.of(2022, 1, 1))
                        .build())
        );
        assertEquals("Адрес электронной почты не может быть пустым.",
                exception.getMessage());

        exception = assertThrows(
                ValidationException.class,
                () -> uc.create(User.builder()
                        .email("mail.ru")
                        .login("login")
                        .name("name")
                        .birthday(LocalDate.of(2022, 1, 1))
                        .build())
        );
        assertEquals("Адрес электронной почты должен содержать символ @.",
                exception.getMessage());
    }

    @Test
    void loginValidTest() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> uc.create(User.builder()
                        .email("email@mail.ru")
                        .login("")
                        .name("name")
                        .birthday(LocalDate.of(2022, 1, 1))
                        .build())
        );
        assertEquals("Логин не может быть пустым и содержать пробелы.",
                exception.getMessage());
    }

    @Test
    void nameValidTest() {
        uc.create(User.builder()
                .email("email@mail.ru")
                .login("login")
                .name("")
                .birthday(LocalDate.of(2022, 1, 1))
                .build());
        User user = uc.getUser(1L);
        assertEquals(user.getName(), "login");
    }

    @Test
    void birthdayValidTest() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> uc.create(User.builder()
                        .email("email@mail.ru")
                        .login("login")
                        .name("name")
                        .birthday(LocalDate.now().plusDays(1))
                        .build())
        );
        assertEquals("День рождения не может быть больше текущей даты.",
                exception.getMessage());
    }
}