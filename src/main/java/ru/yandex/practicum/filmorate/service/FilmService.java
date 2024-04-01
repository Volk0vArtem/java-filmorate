package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final InMemoryFilmStorage storage;

    @Autowired
    public FilmService(InMemoryFilmStorage storage) {
        this.storage = storage;
    }

    public void addLike(int filmId, int userId) {
        storage.getFilm(filmId).getLikes().add(userId);
    }

    public void removeLike(int filmId, int userId) {
        storage.getFilm(filmId).getLikes().remove(userId);
    }

    public List<Film> getTopFilms() {
        List<Film> list = List.copyOf(storage.getFilms());
        return list.stream()
                .sorted((film1, film2) -> {
                    int likesDiff = film1.getLikes().size() - film2.getLikes().size();
                    if (likesDiff != 0) {
                        return likesDiff;
                    }
                    return film1.getName().compareTo(film2.getName());
                })
                .limit(10)
                .collect(Collectors.toList());
    }
}
