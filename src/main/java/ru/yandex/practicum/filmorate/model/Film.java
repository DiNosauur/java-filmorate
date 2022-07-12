package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    public static final int MAX_DESCRIPTION_LEN = 200;
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private Long id;
    private Set<Long> likes;
    @NotNull
    @NotBlank
    private String name;
    @Size(max = MAX_DESCRIPTION_LEN, message = "Description too long!")
    private String description;
    @NotNull
    @Past(message = "Invalid past releaseDate.")
    private LocalDate releaseDate;
    private int duration;

    public Set<Long> getLikes() {
        if (likes == null) {
            likes = new HashSet<>();
        }
        return likes;
    }

    public int compareByLike(Film f) {
        return getLikes().size() < f.getLikes().size() ? 1 : (getLikes().size() == f.getLikes().size() ? 0 : -1);
    }
}