package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    void getGenres() {
        GenreDbStorage storage = new GenreDbStorage(jdbcTemplate);

        assertThat(storage.getGenres())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(
                        new Genre(1, "Комедия"),
                        new Genre(2, "Драма"),
                        new Genre(3, "Мультфильм"),
                        new Genre(4, "Триллер"),
                        new Genre(5, "Документальный"),
                        new Genre(6, "Боевик")));
    }

    @Test
    void getGenre() {
        GenreDbStorage storage = new GenreDbStorage(jdbcTemplate);

        assertThat(storage.getGenre(4))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(new Genre(4, "Триллер"));

    }

}