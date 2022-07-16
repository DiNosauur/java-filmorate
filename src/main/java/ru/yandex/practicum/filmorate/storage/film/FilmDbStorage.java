package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("db")
@Primary
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MPAStorage mpa;
    private final GenreStorage genres;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, MPAStorage mpa, GenreStorage genres)
    {
        this.jdbcTemplate = jdbcTemplate;
        this.mpa = mpa;
        this.genres = genres;
    }


    @Override
    public Collection<Film> findAll() {
        String sql = "select * from FILMS";
        return jdbcTemplate.query(sql, this::makeFilm);
    }

    @Override
    public Film createFilm(Film film) {
        String sqlQuery = "insert into FILMS(NAME, DESCRIPTION, DURATION, RELEASEDATE, MPA_ID ) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setInt(3, film.getDuration());
            stmt.setDate(4, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());
        List<Genre> genreList = film.getGenres().stream()
                .sorted(Genre::compareById)
                .distinct()
                .collect(Collectors.toList());
        film.setGenres(genreList);
        for (Genre genre : genreList) {
            jdbcTemplate.update("insert into FILM_GENRES(FILM_ID, GENRE_ID) " +
                    "values (?, ?)", film.getId(), genre.getId());
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "update FILMS set NAME = ?, " +
                "DESCRIPTION = ?, DURATION = ?, RELEASEDATE = ?, MPA_ID = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , film.getDuration()
                , Date.valueOf(film.getReleaseDate())
                ,film.getMpa().getId()
                , film.getId());
        jdbcTemplate.update("delete from FILM_GENRES where FILM_ID = ?", film.getId());
        List<Genre> genreList = film.getGenres().stream()
                .sorted(Genre::compareById)
                .distinct()
                .collect(Collectors.toList());
        film.setGenres(genreList);
        for (Genre genre : genreList) {
            jdbcTemplate.update("insert into FILM_GENRES(FILM_ID, GENRE_ID) " +
                    "values (?, ?)", film.getId(), genre.getId());
        }
        return film;
    }

    @Override
    public Film getFilm(Long id) {
        String sql = "select * from films where id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::makeFilm, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void addLike(Long id, Long userId) {
        String sqlQuery = "insert into LIKES (FILM_ID, USER_ID) " +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    @Override
    public void delLike(Long id, Long userId) {
        String sqlQuery = "delete from LIKES where FILM_ID = ? and USER_ID = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    @Override
    public Collection<Long> getLikes(Long filmId) {
        String sql = "select USER_ID as ID from LIKES where FILM_ID = ?";
        return jdbcTemplate.query(sql, this::makeUserId, filmId);
    }

    private Long makeUserId(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong("id");
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getLong("id"))
                .description(rs.getString("description"))
                .name(rs.getString("name"))
                .duration(rs.getInt("duration"))
                .releaseDate(rs.getDate("releaseDate").toLocalDate())
                .mpa(mpa.getMPA(rs.getInt("mpa_id")))
                .genres(genres.getFilmGenre(rs.getLong("id")))
                .likes(getLikes(rs.getLong("id")))
                .build();
    }
}
