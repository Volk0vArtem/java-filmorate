package ru.yandex.practicum.filmorate.storage.dao;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

@Component
@Data
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {
        jdbcTemplate.update("INSERT INTO public.films (\"name\", description, \"releaseDate\", duration, rating)" +
                        "VALUES(?, ?, ?, ?, ?, ?);",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        jdbcTemplate.update("INSERT INTO public.films (\"name\", description, \"releaseDate\", duration, rating)" +
                        "VALUES(?, ?, ?, ?, ?, ?);",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());
        return film;
    }

    @Override
    public List<Film> getFilms() {
        List<Film> filmsList = jdbcTemplate.query("select f.id, f.name, f.description, f.\"releaseDate\" , f.duration, " +
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
                filmsMap.get(key).getGenre().add(genre.get(key));
            }
        }
        return filmsList;
    }

    @Override
    public Film getFilm(int id) {
        String sql = "select f.id, f.name,  f.description, f.\"releaseDate\" , f.duration, g.name as genre, r.name as rating, " +
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
            film.setReleaseDate(LocalDate.from(formatter.parse(rs.getString("releaseDate"))));
            film.setDuration(rs.getInt("duration"));
            film.setMpa(new Rating(rs.getInt("rating_id"),rs.getString("rating")));
            do {
                Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("genre"));
                int like = rs.getInt("user_likes");
                if (!film.getGenre().contains(genre)) {
                    film.getGenre().add(genre);
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
            film.setReleaseDate(LocalDate.from(formatter.parse(rs.getString("releaseDate"))));
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
