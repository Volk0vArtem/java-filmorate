package ru.yandex.practicum.filmorate.storage.inMemory;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Data
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Integer, Film> films = new HashMap<>();
    private final UserStorage userStorage;
    private int idGenerator = 1;

    @Override
    public Film addFilm(Film film) {
        film.setId(idGenerator++);
        films.put(film.getId(), film);
        log.info("Добавлен фильм id={}", film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
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

    @Override
    public Film addLike(int filmId, int userId) {
        if (userStorage.getUser(userId) == null) throw new NotFoundException("Пользователь не найден");
        getFilm(filmId).getLikes().add(userId);
        return getFilm(filmId);
    }

    @Override
    public Film removeLike(int filmId, int userId) {
        if (userStorage.getUser(userId) == null) throw new NotFoundException("Пользователь не найден");
        getFilm(filmId).getLikes().remove(userId);
        return getFilm(filmId);
    }

    @Override
    public List<Film> getTopFilms(String c) {
        int count = 10;
        if (c != null) count = Integer.parseInt(c);
        List<Film> list = List.copyOf(getFilms());
        return list.stream()
                .sorted((film1, film2) -> {
                    int likesDiff = film2.getLikes().size() - film1.getLikes().size();
                    if (likesDiff != 0) {
                        return likesDiff;
                    }
                    return film1.getName().compareTo(film2.getName());
                })
                .limit(count)
                .collect(Collectors.toList());
    }
}
