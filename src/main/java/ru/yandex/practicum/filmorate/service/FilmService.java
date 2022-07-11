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
        log.debug(String.format("Сохранение фильма %s", film.getName()));
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        validate(film);
        log.debug(String.format("Изменение фильма %s", film.getName()));
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(Long id) {
        log.debug(String.format("Получение фильма с id = %s", id));
        return filmStorage.getFilm(id);
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
        log.debug(String.format("Пользователь (id = %s) поставил лайк фильму (id = %s)", userId, id));
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
        log.debug(String.format("Пользователь (id = %s) удалил лайк фильму (id = %s)", userId, id));
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted(Film::compareByLike)
                .limit(count)
                .collect(Collectors.toList());
    }
}
