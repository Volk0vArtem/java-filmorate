package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;


public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    User getUser(int id);

    User addToFriends(int id, int friendId);

    void removeFromFriends(int id, int friendId);

    List<User> getFriends(int id);

    List<User> getCommonFriends(int userId, int friendId);

}
