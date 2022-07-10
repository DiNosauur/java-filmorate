package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Builder;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private Long id;
    private Set<Long> friends;
    @Email
    private String email;
    private String name;
    @NotNull
    @NotBlank
    private String login;
    @NotNull
    @Past(message = "Invalid past date birthday.")
    private LocalDate birthday;

    public String getName() {
        if(name.isBlank()) {
            return login;
        }
        return name;
    }

    public Set<Long> getFriends() {
        if (friends == null) {
            friends = new HashSet<>();
        }
        return friends;
    }
}