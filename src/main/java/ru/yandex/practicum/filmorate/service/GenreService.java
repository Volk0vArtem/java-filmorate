package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage storage;

    public List<Genre> getGenres() {
        return storage.getGenres();
    }

    public Genre getGenre(int id) {
        return storage.getGenre(id);
    }
}
