package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage storage;

    public User addToFriends(int userId, int friendId) {
        User user = storage.getUser(userId);
        User friend = storage.getUser(friendId);
        //user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        return user;
    }

    public void removeFromFriends(int userId, int friendId) {
        User user = storage.getUser(userId);
        User friend = storage.getUser(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(int id) {
        return storage.getUser(id).getFriends().stream()
                .map(storage::getUser)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int user1Id, int user2Id) {
        Set<Integer> commonFriends = new HashSet<>(storage.getUser(user1Id).getFriends());
        commonFriends.retainAll(storage.getUser(user2Id).getFriends());
        System.out.println(commonFriends);
        return commonFriends.stream()
                .map(storage::getUser)
                .collect(Collectors.toList());
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

    //????
    public Boolean confirmFriendship(int id, int friendId) {
        return storage.getUser(id).getFriends().contains(friendId) && storage.getUser(friendId).getFriends().contains(id);
    }

}
