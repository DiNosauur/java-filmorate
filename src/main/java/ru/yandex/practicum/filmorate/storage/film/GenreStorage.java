package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreStorage {

    Collection<Genre> findAll();

    Genre getGenre(int id);

    Collection<Genre> getFilmGenre(Long filmId);
}
