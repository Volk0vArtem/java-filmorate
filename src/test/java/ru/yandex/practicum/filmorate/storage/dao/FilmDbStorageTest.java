package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    @Test
    void addAndGetFilm() {
        clearDb();
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        FilmDbStorage storage = new FilmDbStorage(jdbcTemplate, userDbStorage);

        Film film = createFilm();
        storage.addFilm(film);
        Film savedFilm = storage.getFilm(1);

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    void updateFilm() {
        clearDb();
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        FilmDbStorage storage = new FilmDbStorage(jdbcTemplate, userDbStorage);

        Film film = createFilm();
        film = storage.addFilm(film);

        film.setName("newName");
        storage.updateFilm(film);
        Film savedFilm = storage.getFilm(1);

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);

    }

    @Test
    void getFilms() {
        clearDb();
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        FilmDbStorage storage = new FilmDbStorage(jdbcTemplate, userDbStorage);

        Film film1 = createFilm();
        film1 = storage.addFilm(film1);

        Film film2 = createFilm();
        film2 = storage.addFilm(film2);

        assertThat(storage.getFilms())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(film1,film2));
    }

    @Test
    void addAndRemoveLike() {
        clearDb();
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        FilmDbStorage storage = new FilmDbStorage(jdbcTemplate, userDbStorage);

        Film film = createFilm();
        film = storage.addFilm(film);

        User user = createUser();
        user = userDbStorage.addUser(user);

        storage.addLike(1,1);

        assertThat(storage.getFilm(1).getLikes())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(Set.of(1));

        storage.removeLike(1,1);
        assertThat(storage.getFilm(1).getLikes())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(new HashSet<>());
    }


    @Test
    void getTopFilms() {
        clearDb();
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        FilmDbStorage storage = new FilmDbStorage(jdbcTemplate, userDbStorage);

        User user1 = createUser();
        User user2 = createUser();
        user2.setName("user2");
        User user3 = createUser();
        user3.setName("user3");
        User user4 = createUser();
        user4.setName("user4");
        User user5 = createUser();
        user5.setName("user5");
        userDbStorage.addUser(user1);
        userDbStorage.addUser(user2);
        userDbStorage.addUser(user3);
        userDbStorage.addUser(user4);
        userDbStorage.addUser(user5);

        Film film1 = createFilm();
        Film film2 = createFilm();
        film2.setName("film2");
        Film film3 = createFilm();
        film3.setName("film3");
        Film film4 = createFilm();
        film4.setName("film4");
        storage.addFilm(film1);
        storage.addFilm(film2);
        storage.addFilm(film3);
        storage.addFilm(film4);

        film1 = storage.addLike(1,1);

        storage.addLike(2,1);
        film2 = storage.addLike(2,2);

        storage.addLike(3,1);
        storage.addLike(3,2);
        storage.addLike(3,3);
        storage.addLike(3,4);
        film3 = storage.addLike(3,5);

        storage.addLike(4,1);
        storage.addLike(4,2);
        film4 = storage.addLike(4,3);


        assertThat(storage.getTopFilms(null))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(film3, film4, film2, film1));

        assertThat(storage.getTopFilms("2"))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(film3, film4));
    }

    private Film createFilm(){
        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setDuration(120);
        film.setMpa(new Rating(1));
        film.getGenres().add(new Genre(3));
        film.getGenres().add(new Genre(5));
        film.setReleaseDate(LocalDate.of(1955,10,10));
        return film;
    }

    private User createUser(){
        User user1 = new User();
        user1.setName("name");
        user1.setLogin("login");
        user1.setEmail("email");
        user1.setBirthday(LocalDate.of(2000,1,1));
        return user1;
    }

    private void clearDb(){
        jdbcTemplate.update("DELETE FROM films;");
        jdbcTemplate.update("ALTER TABLE films ALTER COLUMN id RESTART WITH 1;");
        jdbcTemplate.update("DELETE FROM users;");
        jdbcTemplate.update("ALTER TABLE users ALTER COLUMN id RESTART WITH 1;");
        jdbcTemplate.update("TRUNCATE TABLE friends;");
        jdbcTemplate.update("TRUNCATE TABLE likes;");
        jdbcTemplate.update("TRUNCATE TABLE film_genre;");
    }
}