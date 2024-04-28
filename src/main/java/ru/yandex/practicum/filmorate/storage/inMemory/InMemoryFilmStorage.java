package ru.yandex.practicum.filmorate.storage.inMemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Integer, Film> films = new HashMap<>();
    private int idGenerator = 1;

    @Override
    public Film addFilm(Film film) {
        film.setId(idGenerator++);
        films.put(film.getId(), film);
        log.info("Добавлен фильм id={}", film.getId());
        return film;
    }

    @Override
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.info("Попытка изменения несуществующего фильма id={}", film.getId());
            throw new NotFoundException("Такого фильма не существует");
        }
        films.put(film.getId(), film);
        log.info("Изменён фильм id={}", film.getId());
        return film;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilm(int id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм не найден");
        }
        return films.get(id);
    }
}
