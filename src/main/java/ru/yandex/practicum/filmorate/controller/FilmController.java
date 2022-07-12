package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.*;


@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film put(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable("id") Long id){
        return filmService.getFilm(id);
    }

    //PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        filmService.addLike(id, userId);
    }

    //DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.
    @DeleteMapping("/{id}/like/{userId}")
    public void delLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        filmService.delLike(id, userId);
    }

    //GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков.
    // Если значение параметра count не задано, верните первые 10.
    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(
            @RequestParam(defaultValue = "10", required = false) Integer count
    ) {
        return filmService.getPopularFilms(count);
    }

}