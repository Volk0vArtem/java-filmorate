package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.annotations.ReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    private int id;
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200)
    @NotBlank(message = "Описание фильма не может быть пустым")
    private String description;

    @ReleaseDate
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private ArrayList<Genre> genres = new ArrayList<>();
    private Rating mpa;
    private final Set<Integer> likes = new HashSet<>();

}