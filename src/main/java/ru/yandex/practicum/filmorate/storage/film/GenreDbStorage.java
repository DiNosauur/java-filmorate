package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final Map<Integer, Genre> genres = new HashMap<>();

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from GENRES");
        while (userRows.next()) {
            genres.put(userRows.getInt("id")
                    , Genre.builder()
                            .id(userRows.getInt("id"))
                            .name(userRows.getString("name"))
                            .build());
        }
    }

    @Override
    public Collection<Genre> findAll() {
        return genres.values();
    }

    @Override
    public Genre getGenre(int id) {
        return genres.get(id);
    }

    @Override
    public Collection<Genre> getFilmGenre(Long filmId) {
        String sql = "select G.* from FILM_GENRES F, GENRES G " +
                "where F.FILM_ID = ? AND G.ID = F.GENRE_ID";
        return jdbcTemplate.query(sql, this::makeGenre, filmId);
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }
}
