package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film createFilm(Film film) {
        validate(film);
        if (film.getId() != null && film.getId() < 0) {
            throw new ValidationException("Идентификатор фильма не может быть отрицательным.");
        }
        log.debug("Сохранение фильма {}", film.getName());
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        validate(film);
        if (film.getId() == null) {
            throw new ValidationException("Идентификатор фильма не может быть пустым.");
        }
        if (getFilm(film.getId()) == null) {
            throw new NotFoundException(String.format(
                    "Фильм (id = %s) не найден",
                    film.getId()));
        }
        log.debug("Изменение фильма {}", film.getName());
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(Long id) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            throw new NotFoundException(String.format(
                    "Фильм (id = %s) не найден",
                    id));
        }
        log.debug("Получение фильма с id = {}", id);
        return film;
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription().length() > Film.MAX_DESCRIPTION_LEN) {
            throw new ValidationException("Максимальная длина описания не должна быть больше 200 символов.");
        }
        if (film.getReleaseDate().isBefore(Film.MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше даты рождения кино.");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
    }

    public void addLike(Long id, Long userId) {
        if (filmStorage.getFilm(id) == null) {
            throw new NotFoundException(String.format(
                    "Фильм (id = %s) не найден",
                    id));
        }
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException(String.format(
                    "Пользователь (id = %s) не найден",
                    userId));
        }
        filmStorage.addLike(id, userId);
        log.debug("Пользователь (id = {}) поставил лайк фильму (id = {})", userId, id);
    }

    public void delLike(Long id, Long userId) {
        if (filmStorage.getFilm(id) == null) {
            throw new NotFoundException(String.format(
                    "Фильм (id = %s) не найден",
                    id));
        }
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException(String.format(
                    "Пользователь (id = %s) не найден",
                    userId));
        }
        filmStorage.delLike(id, userId);
        log.debug("Пользователь (id = {}) удалил лайк фильму (id = {})", userId, id);
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted(Film::compareByLike)
                .limit(count)
                .collect(Collectors.toList());
    }
}
