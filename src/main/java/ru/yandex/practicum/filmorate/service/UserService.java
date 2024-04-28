package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage storage;

    public User addToFriends(int userId, int friendId) {
        return storage.addToFriends(userId, friendId);
    }

    public void removeFromFriends(int userId, int friendId) {
        storage.removeFromFriends(userId, friendId);
    }

    public List<User> getFriends(int id) {
        return storage.getFriends(id);
    }

    public List<User> getCommonFriends(int user1Id, int user2Id) {
        return storage.getCommonFriends(user1Id, user2Id);
    }

    public List<User> getUsers() {
        return storage.getUsers();
    }

    public User getUser(int id) {
        return storage.getUser(id);
    }

    public User addUser(User user) {
        return storage.addUser(user);
    }

    public User updateUser(User user) {
        return storage.updateUser(user);
    }

}
