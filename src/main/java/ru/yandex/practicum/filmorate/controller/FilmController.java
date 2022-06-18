package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private Long sequenceId = 1l;
    private final Map<Long, Film> films = new HashMap<>();

    private List<Film> getAllFilms() {
        List<Film> filmList = new ArrayList<>();
        for (Film film : films.values()) {
            filmList.add(film);
        }
        return filmList;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return getAllFilms();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        if(film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if(film.getDescription().length() > film.getMAX_DESCRIPTION_LEN()) {
            throw new ValidationException("Максимальная длина описания не должна быть больше 200 символов.");
        }
        if(film.getReleaseDate().isBefore(film.getMIN_RELEASE_DATE())) {
            throw new ValidationException("Дата релиза не может быть раньше даты рождения кино.");
        }
        if(film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
        if(film.getId() == null) {
            film.setId(sequenceId++);
        } else if(film.getId() < 0) {
            throw new ValidationException("Идентификатор фильма не может быть отрицательным.");
        }
        films.put(film.getId(), film);
        log.info("Добавлен фильм {}", film.getName());
        return film;
    }

    @PutMapping
    public Film put(@RequestBody Film film) {
        if(film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if(film.getId() == null || film.getId() < 0) {
            throw new ValidationException("Идентификатор фильма не может быть пустым или отрицательным.");
        }
        if(film.getReleaseDate().isBefore(film.getMIN_RELEASE_DATE())) {
            throw new ValidationException("Дата релиза не может быть раньше даты рождения кино.");
        }
        if(film.getDescription().length() > film.getMAX_DESCRIPTION_LEN()) {
            throw new ValidationException("Максимальная длина описания не должна быть больше 200 символов.");
        }
        if(film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
        films.put(film.getId(), film);
        log.info("Обновлён фильм с id = {}", film.getId());
        return film;
    }
}