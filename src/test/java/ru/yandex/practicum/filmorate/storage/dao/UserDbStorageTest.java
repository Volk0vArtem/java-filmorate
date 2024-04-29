package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final JdbcTemplate jdbcTemplate;

    @Test
    void addAndGetUser() {
        clearDb();
        UserDbStorage storage = new UserDbStorage(jdbcTemplate);

        User user = createUser();

        storage.addUser(user);

        User savedUser = storage.getUser(1);

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    void updateUser() {
        clearDb();
        UserDbStorage storage = new UserDbStorage(jdbcTemplate);

        User user = createUser();
        user = storage.addUser(user);

        user.setName("newName");
        storage.updateUser(user);

        User savedUser = storage.getUser(1);
        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    void getUsers() {
        clearDb();
        UserDbStorage storage = new UserDbStorage(jdbcTemplate);

        User user1 = createUser();
        User user2 = createUser();
        user2.setName("user2");

        storage.addUser(user1);
        storage.addUser(user2);

        List<User> savedUsers = storage.getUsers();

        assertThat(savedUsers)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(user1,user2));
    }

    @Test
    void addAndGetFriends() {
        clearDb();
        UserDbStorage storage = new UserDbStorage(jdbcTemplate);

        User user1 = createUser();
        User user2 = createUser();
        user2.setName("user2");

        storage.addUser(user1);
        storage.addUser(user2);
        storage.addToFriends(1,2);

        List<User> savedFriends = storage.getFriends(1);

        assertThat(savedFriends)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(user2));

    }

    @Test
    void removeFromFriends() {
        clearDb();
        UserDbStorage storage = new UserDbStorage(jdbcTemplate);

        User user1 = createUser();
        User user2 = createUser();
        user2.setName("user2");
        User user3 = createUser();
        user3.setName("user3");

        storage.addUser(user1);
        storage.addUser(user2);
        storage.addUser(user3);
        storage.addToFriends(1,2);
        storage.addToFriends(1,3);
        storage.removeFromFriends(1,3);

        List<User> savedFriends = storage.getFriends(1);

        assertThat(savedFriends)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(user2));

    }

    @Test
    void getCommonFriends() {
        clearDb();
        UserDbStorage storage = new UserDbStorage(jdbcTemplate);

        User user1 = createUser();
        User user2 = createUser();
        user2.setName("user2");
        User user3 = createUser();
        user3.setName("user3");

        storage.addUser(user1);
        storage.addUser(user2);
        storage.addUser(user3);

        storage.addToFriends(1,2);
        storage.addToFriends(3,2);

        List<User> savedCommonFriends = storage.getCommonFriends(1,3);

        assertThat(savedCommonFriends)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(user2));
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
        jdbcTemplate.update("DELETE FROM users;");
        jdbcTemplate.update("ALTER TABLE users ALTER COLUMN id RESTART WITH 1;");
        jdbcTemplate.update("TRUNCATE TABLE friends;");
    }
}