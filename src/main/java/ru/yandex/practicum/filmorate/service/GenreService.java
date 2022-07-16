package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;

import java.util.Collection;

@Slf4j
@Service
public class GenreService {
    private final GenreStorage genres;

    @Autowired
    public GenreService(GenreStorage genres) {
        this.genres = genres;
    }

    public Collection<Genre> getAllGenre() {
        return genres.findAll();
    }

    public Genre getGenre(int id) {
        Genre genre = genres.getGenre(id);
        if (genre == null) {
            throw new NotFoundException(String.format(
                    "Жанр (id = %s) не найден",
                    id));
        }
        log.debug("Получение жанра с id = {}", id);
        return genre;
    }

}
