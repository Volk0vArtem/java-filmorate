package ru.yandex.practicum.filmorate.storage.dao;

import lombok.Data;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Component
@Data
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getGenres() {
        return jdbcTemplate.query("select * from genre order by id", genreRowMapper());
    }

    @Override
    public Genre getGenre(int id) {
        try {
            return jdbcTemplate.queryForObject("select * from genre where id=?", genreRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Жанр с id=" + id + " не найден");
        }
    }


    private RowMapper<Genre> genreRowMapper() {
        return (rs, rowNum) -> new Genre(rs.getInt("id"), rs.getString("name"));
    }
}
