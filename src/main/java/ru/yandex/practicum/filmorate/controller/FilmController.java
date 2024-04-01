package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final InMemoryFilmStorage storage;

    @Autowired
    public FilmController(InMemoryFilmStorage storage) {
        this.storage = storage;
    }

    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {
        return storage.addFilm(film);
    }

    @PutMapping
    public Film putFilm(@Valid @RequestBody Film film) throws ValidationException {
        return storage.updateFilm(film);
    }

    @GetMapping
    public List<Film> getFilms() {
        return storage.getFilms();
    }

}
