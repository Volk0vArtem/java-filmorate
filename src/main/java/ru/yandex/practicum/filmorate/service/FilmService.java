package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage storage;

    @Autowired
    public FilmService(FilmStorage storage) {
        this.storage = storage;
    }

    public Film addLike(int filmId, int userId) {
        storage.getFilm(filmId).getLikes().add(userId);
        return storage.getFilm(filmId);
    }

    public Film removeLike(int filmId, int userId) {
        storage.getFilm(filmId).getLikes().remove(userId);
        return storage.getFilm(filmId);
    }

    public List<Film> getTopFilms(Integer count) {
        if (count == null) count = 10;
        List<Film> list = List.copyOf(storage.getFilms());
        return list.stream()
                .sorted((film1, film2) -> {
                    int likesDiff = film1.getLikes().size() - film2.getLikes().size();
                    if (likesDiff != 0) {
                        return likesDiff;
                    }
                    return film1.getName().compareTo(film2.getName());
                })
                .limit(count)
                .collect(Collectors.toList());
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
