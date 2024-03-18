package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/film")
@Slf4j
public class FilmController {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private int idGenerator = 1;

    @PostMapping
    public Film postFilm( @RequestBody Film film) {

        film.setId(idGenerator++);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film putFilm( @RequestBody Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @GetMapping
    public List<Film> getFilms(){
        return new ArrayList<>(films.values());
    }

}
