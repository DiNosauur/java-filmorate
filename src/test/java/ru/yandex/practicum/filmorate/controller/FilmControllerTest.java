package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController fc;

    @BeforeEach
    public void BeforeEach() {
        fc = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage()));
    }

    @Test
    void createTest() {
        fc.create(Film.builder()
                .name("Кин-дза-дза!")
                .description("трагикомедия в жанре фантастической антиутопии")
                .releaseDate(LocalDate.of(1986, 12, 1))
                .duration(127)
                .build());
        Collection<Film> films = fc.findAll();
        assertEquals(1, films.size(), "Неверное количество фильмов.");
    }

    @Test
    void nameValidTest() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> fc.create(Film.builder()
                        .name("")
                        .description("трагикомедия в жанре фантастической антиутопии")
                        .releaseDate(LocalDate.of(1986, 12, 1))
                        .duration(127)
                        .build())
        );
        assertEquals("Название фильма не может быть пустым.",
                exception.getMessage());
    }

    @Test
    void descriptionValidTest() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> fc.create(Film.builder()
                        .name("Кин-дза-дза!")
                        .description("советская двухсерийная трагикомедия в жанре фантастической антиутопии, " +
                                "снятая режиссёром Георгием Данелией на «Мосфильме» в 1986 году. " +
                                "Лента оказала влияние на русскоязычную культуру — вымышленные слова из фильма вошли " +
                                "в разговорный язык, некоторые фразы персонажей стали устойчивыми выражениями.")
                        .releaseDate(LocalDate.of(1986, 12, 1))
                        .duration(127)
                        .build())
        );
        assertEquals("Максимальная длина описания не должна быть больше 200 символов.",
                exception.getMessage());
    }

    @Test
    void releaseDateValidTest() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> fc.create(Film.builder()
                        .name("Кин-дза-дза!")
                        .description("трагикомедия в жанре фантастической антиутопии")
                        .releaseDate(LocalDate.of(1886, 12, 1))
                        .duration(127)
                        .build())
        );
        assertEquals("Дата релиза не может быть раньше даты рождения кино.",
                exception.getMessage());
    }

    @Test
    void durationValidTest() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> fc.create(Film.builder()
                        .name("Кин-дза-дза!")
                        .description("трагикомедия в жанре фантастической антиутопии")
                        .releaseDate(LocalDate.of(1986, 12, 1))
                        .duration(0)
                        .build())
        );
        assertEquals("Продолжительность фильма должна быть положительной.",
                exception.getMessage());
    }
}