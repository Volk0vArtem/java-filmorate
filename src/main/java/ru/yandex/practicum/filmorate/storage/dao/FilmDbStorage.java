package ru.yandex.practicum.filmorate.storage.dao;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Data
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {

        Rating rating = jdbcTemplate.queryForObject("select * from rating where id = ?",
                (rs, rowNum) -> new Rating(rs.getInt("id"), rs.getString("name")), film.getMpa().getId());
        film.setMpa(rating);

        Map<String, Object> values = new HashMap<>();
        values.put("id", film.getId());
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("rating", film.getMpa().getId());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");

        film.setId(simpleJdbcInsert.executeAndReturnKey(values).intValue());

        SimpleJdbcInsert simpleJdbcInsertGenres = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film_genre");
        Map<String, Object> genres = new HashMap<>();
        for (Genre genre : film.getGenres()){
            genres.put("film_id", film.getId());
            genres.put("genre_id", genre.getId());
            simpleJdbcInsertGenres.execute(genres);
        }



        List<Genre> genreList = jdbcTemplate.query("select fg.genre_id, g.name from film_genre fg join genre g on fg.genre_id=g.id where film_id = ?",
                (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("name")), film.getId());

        film.setGenres(new ArrayList<>(genreList));

        film.setMpa(rating);

        return film;
    }

    @Override
    public Film updateFilm(Film film) {

        Rating rating = jdbcTemplate.queryForObject("select * from rating where id = ?",
                (rs, rowNum) -> new Rating(rs.getInt("id"), rs.getString("name")), film.getMpa().getId());
        film.setMpa(rating);
        film.setMpa(rating);

        jdbcTemplate.update("update films set name=?, description=?, release_date=?, duration=?, rating=? where id=?",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        return film;
    }

    @Override
    public List<Film> getFilms() {
        List<Film> filmsList = jdbcTemplate.query("select f.id, f.name, f.description, f.\"release_date\" , f.duration, " +
                        "r.name as rating_name, f.rating as rating_id from films f join rating r on f.rating=r.id order by f.id",
                filmsRowMapper());
        HashMap<Integer, Film> filmsMap = new HashMap<>();
        for (Film film : filmsList) {
            filmsMap.put(film.getId(), film);
        }
        List<Integer[]> likes = jdbcTemplate.query("select * from likes", likesRowMapper());
        List<HashMap<Integer, Genre>> genres = jdbcTemplate.query("select fg.film_id, fg.genre_id, g.name as genre_name " +
                        "from film_genre fg join genre g on fg.genre_id=g.id", userGenreRowMapper());

        for (Integer[] like : likes) {
            filmsMap.get(like[0]).getLikes().add(like[1]);
        }

        for (HashMap<Integer, Genre> genre : genres) {
            for (Integer key : genre.keySet()) {
                filmsMap.get(key).getGenres().add(genre.get(key));
            }
        }
        return filmsList;
    }

    @Override
    public Film getFilm(int id) {
        String sql = "select f.id, f.name,  f.description, f.\"release_date\" , f.duration, g.name as genre, r.name as rating, " +
                "l.user_id as user_likes, r.id as rating_id, g.id as genre_id from films f join rating r on f.rating=r.id " +
                "join film_genre fg on f.id=fg.film_id join genre g on fg.genre_id=g.id join likes l on f.id=l.film_id where f.id=?";
        return jdbcTemplate.queryForObject(sql, filmRowMapper(), id);
    }

    private RowMapper<Film> filmRowMapper() {
        return (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getInt("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            film.setReleaseDate(LocalDate.from(formatter.parse(rs.getString("release_date"))));
            film.setDuration(rs.getInt("duration"));
            film.setMpa(new Rating(rs.getInt("rating_id"),rs.getString("rating")));
            do {
                Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("genre"));
                int like = rs.getInt("user_likes");
                if (!film.getGenres().contains(genre)) {
                    film.getGenres().add(genre);
                }
                if (like != 0) {
                    film.getLikes().add(like);
                }

            } while (rs.next());
            return film;
        };
    }

    private RowMapper<Film> filmsRowMapper() {
        return (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getInt("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            film.setReleaseDate(LocalDate.from(formatter.parse(rs.getString("release_date"))));
            film.setDuration(rs.getInt("duration"));
            film.setMpa(new Rating(rs.getInt("rating_id"),rs.getString("rating_name")));
            return film;
        };
    }

    private RowMapper<Integer[]> likesRowMapper() {
        return (rs, rowNum) -> {
            Integer[] like = new Integer[2];
            like[0] = rs.getInt("film_id");
            like[1] =rs.getInt("user_id");
            return like;
        };
    }

    private RowMapper<HashMap<Integer, Genre>> userGenreRowMapper() {
        return (rs, rowNum) -> {
            HashMap<Integer, Genre> genre = new HashMap<>();
            genre.put(rs.getInt("film_id"),
                    new Genre(rs.getInt("genre_id"), rs.getString("genre_name")));
            return genre;
        };
    }

}
