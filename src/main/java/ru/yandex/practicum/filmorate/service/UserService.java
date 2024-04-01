package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final InMemoryUserStorage storage;

    @Autowired
    public UserService(InMemoryUserStorage storage) {
        this.storage = storage;
    }

    public void addToFriends(int userId, int friendId) {
        User user = storage.getUser(userId);
        User friend = storage.getUser(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFromFriends(int userId, int friendId) {
        User user = storage.getUser(userId);
        User friend = storage.getUser(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<Integer> getCommonFriendsList(int user1Id, int user2Id) {
        Set<Integer> commonFriends = new HashSet<>(storage.getUser(user1Id).getFriends());
        commonFriends.retainAll(storage.getUser(user2Id).getFriends());
        return List.copyOf(commonFriends);
    }

}
