package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage storage;

    public Film addLike(int filmId, int userId) {
        return storage.addLike(filmId, userId);
    }

    public Film removeLike(int filmId, int userId) {
        return storage.removeLike(filmId, userId);
    }

    public List<Film> getTopFilms(String c) {
        return storage.getTopFilms(c);
    }

    public Film addFilm(Film film) {
        return storage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return storage.updateFilm(film);
    }

    public Film getFilm(int id) {
        return storage.getFilm(id);
    }

    public List<Film> getFilms() {
        return storage.getFilms();
    }
}
