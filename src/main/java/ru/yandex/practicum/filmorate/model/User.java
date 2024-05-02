package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    private int id;
    @Email
    private String email;

    @Pattern(regexp = ".*\\S.*")
    private String login;

    private String name;
    @Past
    private LocalDate birthday;
    private final Set<Integer> friends = new HashSet<>();
}
