package ru.yandex.practicum.filmorate.storage.dao;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Data
@Primary
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {

        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("birthday", user.getBirthday());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        user.setId(simpleJdbcInsert.executeAndReturnKey(values).intValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        getUser(user.getId());
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
        for (User u : usersList) {
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
        try {
            return jdbcTemplate.queryForObject("select * from users u left join friends f on u.id=f.user_id where u.id = ?",
                    userRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь c id=" + id + " не найден");
        }
    }

    @Override
    public User addToFriends(int id, int friendId) {
        getUser(id);
        getUser(friendId);
        if (getUser(id).getFriends().contains(friendId)) throw new IllegalArgumentException("Пользователь уже в друзьях");

        Map<String, Object> values = new HashMap<>();
        values.put("user_id", id);
        values.put("friend_id", friendId);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("friends");

        simpleJdbcInsert.execute(values);
        return getUser(id);
    }

    @Override
    public void removeFromFriends(int id, int friendId) {
        getUser(id);
        getUser(friendId);
        jdbcTemplate.update("delete from friends where user_id = ? and friend_id = ?", id, friendId);
    }

    @Override
    public List<User> getFriends(int id) {
        getUser(id);
        return jdbcTemplate.query("select friend_id from friends where user_id=?",
                (rs, rowNum) -> getUser(rs.getInt("friend_id")), id);

    }

    @Override
    public List<User> getCommonFriends(int userId, int friendId) {
        getUser(userId);
        getUser(friendId);
            List<User> userFriends = getFriends(userId);
            List<User> friendFriends = getFriends(friendId);
            return userFriends.stream()
                    .filter(friendFriends::contains)
                    .collect(Collectors.toList());
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
                if (friend != 0) {
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
