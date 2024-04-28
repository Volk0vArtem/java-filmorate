package ru.yandex.practicum.filmorate.storage.dao;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
        List<User> usersList = jdbcTemplate.query("select * from users u order by id",
                usersRowMapper());
        HashMap<Integer, User> usersMap = new HashMap<>();
        for (User u : usersList){
            usersMap.put(u.getId(), u);
        }
        List<Integer[]> friends = jdbcTemplate.query("select * from friends",
                friendsRowMapper());
        for (Integer[] pair : friends) {
            usersMap.get(pair[0]).getFriends().add(pair[1]);
        }
        return usersList;
    }

    @Override
    public User getUser(int id) {
        return jdbcTemplate.queryForObject("select * from users u left join friends f on u.id=f.user_id where u.id = ?",
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
                int friend = rs.getInt("friend_id");
                if (friend != 0){
                    user1.getFriends().add(friend);
                }
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

    private RowMapper<Integer[]> friendsRowMapper() {
        return (rs, rowNum) -> {
            Integer[] pair = new Integer[2];
            pair[0] = (rs.getInt("user_id"));
            pair[1] = (rs.getInt("friend_id"));
            return pair;
        };
    }
}