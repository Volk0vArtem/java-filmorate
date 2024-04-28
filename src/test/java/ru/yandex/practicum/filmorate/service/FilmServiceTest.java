package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.inMemory.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.inMemory.InMemoryUserStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {
    private FilmService service;
    User user1;
    User user2;
    User user3;
    Film film1;
    Film film2;
    Film film3;

    @BeforeEach
    public void beforeEach() {
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        service = new FilmService(new InMemoryFilmStorage(), userStorage);

        user1 = new User();
        user2 = new User();
        user3 = new User();
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        userStorage.addUser(user3);

        film1 = new Film();
        film2 = new Film();
        film3 = new Film();
        service.addFilm(film1);
        service.addFilm(film2);
        service.addFilm(film3);
    }

    @Test
    void addLike() {
        assertEquals(0, film1.getLikes().size());
        service.addLike(1, 2);
        assertEquals(1, film1.getLikes().size());
        assertTrue(film1.getLikes().contains(2));

        assertThrows(NotFoundException.class, () -> service.addLike(1, 4));
    }

    @Test
    void removeLike() {
        service.addLike(1, 2);
        service.removeLike(1, 2);
        assertEquals(0, film1.getLikes().size());
        assertThrows(NotFoundException.class, () -> service.addLike(1, 4));
    }

    @Test
    void getTopFilms() {
        service.addLike(1, 1);
        service.addLike(2, 1);
        service.addLike(2, 2);
        service.addLike(3, 1);
        service.addLike(3, 2);
        service.addLike(3, 3);
        List<Film> top3 = service.getTopFilms(null);
        List<Film> top2 = service.getTopFilms("2");
        assertEquals(3, top3.size());
        assertEquals(2, top2.size());
    }
}