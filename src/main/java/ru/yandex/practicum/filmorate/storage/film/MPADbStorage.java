package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class MPADbStorage implements MPAStorage {

    private final JdbcTemplate jdbcTemplate;
    private final Map<Integer, MPA> mpa = new HashMap<>();

    public MPADbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from MPA");
        while (userRows.next()) {
            mpa.put(userRows.getInt("id")
                    , MPA.builder()
                            .id(userRows.getInt("id"))
                            .name(userRows.getString("name"))
                            .build());
        }
    }

    @Override
    public Collection<MPA> findAll() {
        return mpa.values();
    }

    @Override
    public MPA getMPA(int id) {
        return mpa.get(id);
    }
}
