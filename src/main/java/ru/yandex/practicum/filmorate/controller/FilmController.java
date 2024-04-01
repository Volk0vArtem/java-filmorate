package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;


import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService service;

    @Autowired
    public FilmController(FilmService service) {
        this.service = service;
    }

    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {
        return service.addFilm(film);
    }

    @PutMapping
    public Film putFilm(@Valid @RequestBody Film film) throws ValidationException {
        return service.updateFilm(film);
    }

    @GetMapping
    public List<Film> getFilms() {
        return service.getFilms();
    }

}
