package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Long sequenceId = 1l;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film createFilm(Film film) {
        if (film.getId() == null) {
            film.setId(sequenceId++);
        } else if (film.getId() < 0) {
            throw new ValidationException("Идентификатор фильма не может быть отрицательным.");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            throw new ValidationException("Идентификатор фильма не может быть пустым.");
        }
        if (films.get(film.getId()) == null) {
            throw new NotFoundException(String.format(
                    "Фильм (id = %s) не найден",
                    film.getId()));
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilm(Long id) {
        if (id == null) {
            return null;
        }
        if (films.get(id) == null) {
            throw new NotFoundException(String.format(
                    "Фильм (id = %s) не найден",
                    id));
        }
        return films.get(id);
    }

    @Override
    public void addLike(Long id, Long userId) {
        if (id != null) {
            films.get(id).getLikes().add(userId);
        }
    }

    @Override
    public void delLike(Long id, Long userId) {
        if (id != null) {
            films.get(id).getLikes().remove(userId);
        }
    }

    @Override
    public Collection<Long> getLikes(Long filmId) {
        if (filmId == null || films.get(filmId) == null) {
            return null;
        }
        return films.get(filmId).getLikes();
    }
}
