package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@Data
@Primary
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    @Override
    public User addUser(User user) {
        jdbcTemplate.update("insert into users(email, login, name, birthday) values (?,?,?,?)",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
        return user;
    }

    @Override
    public User updateUser(User user) {
        jdbcTemplate.update("update users set email = ?, login = ?, name = ?, birthday = ? where id = ?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public List<User> getUsers() {
        List<User> users = jdbcTemplate.query("select * from users u order by id",
                usersRowMapper());
        List<List<Integer>> friends = jdbcTemplate.query("select * from friends",
                friendsRowMapper());
        for (List<Integer> pair : friends) {
            users.get(pair.get(0)-1).getFriends().add(pair.get(1));
        }
        return users;
    }

    @Override
    public User getUser(int id) {
        return jdbcTemplate.queryForObject("select * from users u join friends f on u.id=f.user_id where u.id = ?",
                userRowMapper(), id);
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            User user1 = new User();
            user1.setId(rs.getInt("id"));
            user1.setEmail(rs.getString("email"));
            user1.setLogin(rs.getString("login"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            user1.setBirthday(LocalDate.from(formatter.parse(rs.getString("birthday"))));
            user1.setName(rs.getString("name"));
            do {
                user1.getFriends().add(rs.getInt("friend_id"));
            } while (rs.next());
            return user1;
        };
    }

    private RowMapper<User> usersRowMapper() {
        return (rs, rowNum) -> {
            User user1 = new User();
            user1.setId(rs.getInt("id"));
            user1.setEmail(rs.getString("email"));
            user1.setLogin(rs.getString("login"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            user1.setBirthday(LocalDate.from(formatter.parse(rs.getString("birthday"))));
            user1.setName(rs.getString("name"));
            return user1;
        };
    }

    private RowMapper<List<Integer>> friendsRowMapper () {
        return (rs, rowNum) -> {
            List<Integer> pair = new ArrayList<>();
            pair.add(rs.getInt("user_id"));
            pair.add(rs.getInt("friend_id"));
            return pair;
        };
    }
}
