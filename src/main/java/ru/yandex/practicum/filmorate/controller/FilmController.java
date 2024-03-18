package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private int idGenerator = 1;

    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {
        film.setId(idGenerator++);
        films.put(film.getId(), film);
        log.info("Добавлен фильм id={}", film.getId());
        return film;
    }

    @PutMapping
    public Film putFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (!films.containsKey(film.getId())) {
            log.info("Попытка изменения несуществующего фильма id={}", film.getId());
            throw new ValidationException("Такого фильма не существует");
        }
        films.put(film.getId(), film);
        log.info("Изменён фильм id={}", film.getId());
        return film;
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

}
