package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    private final int MAX_DESCRIPTION_LEN = 200;
    private final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private Long id;
    @NotNull
    @NotBlank
    private String name;
    @Size(max = MAX_DESCRIPTION_LEN, message = "Description too long!")
    private String description;
    @NotNull
    @Past(message = "Invalid past releaseDate.")
    private LocalDate releaseDate;
    private int duration;
}