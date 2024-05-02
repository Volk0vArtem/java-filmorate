package ru.yandex.practicum.filmorate.storage.dao;

import lombok.Data;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.List;

@Component
@Data
public class RatingDbStorage implements RatingStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Rating> getRatings() {
        return jdbcTemplate.query("select * from rating order by id", ratingRowMapper());
    }

    @Override
    public Rating getRating(int id) {
        try {
            return jdbcTemplate.queryForObject("select * from rating where id=?", ratingRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Рейтинг с id=" + id + " не найден");
        }
    }

    private RowMapper<Rating> ratingRowMapper() {
        return (rs, rowNum) -> new Rating(rs.getInt("id"), rs.getString("name"));
    }
}
