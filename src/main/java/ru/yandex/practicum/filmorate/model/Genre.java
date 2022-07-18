package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Genre {
    private int id;
    private String name;

    public int compareById(Genre g2) {
        return id > g2.getId() ? 1 : (id == g2.getId() ? 0 : -1);
    }
}
