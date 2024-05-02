package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RatingDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    void getRatings() {
        RatingDbStorage storage = new RatingDbStorage(jdbcTemplate);

        assertThat(storage.getRatings())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(
                        new Rating(1, "G"),
                        new Rating(2, "PG"),
                        new Rating(3, "PG-13"),
                        new Rating(4, "R"),
                        new Rating(5, "NC-17")));
    }

    @Test
    void getRating() {
        RatingDbStorage storage = new RatingDbStorage(jdbcTemplate);
        assertThat(storage.getRating(3))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(new Rating(3, "PG-13"));

    }
}