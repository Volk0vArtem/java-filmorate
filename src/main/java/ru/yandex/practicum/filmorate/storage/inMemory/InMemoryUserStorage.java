package ru.yandex.practicum.filmorate.storage.inMemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Integer, User> users = new HashMap<>();

    private int idGenerator = 1;

    @Override
    public User addUser(User user) {
        user.setId(idGenerator++);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Добавлен пользователь id={}", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.info("Попытка изменения несуществующего пользователя id={}", user.getId());
            throw new NotFoundException("Такого пользователя не существует");
        }
        users.put(user.getId(), user);
        log.info("Изменён пользователь id={}", user.getId());
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
        return users.get(id);
    }

    @Override
    public User addToFriends(int id, int friendId) {
        User user = getUser(id);
        User friend = getUser(friendId);
        friend.getFriends().add(id);
        return user;
    }

    @Override
    public void removeFromFriends(int id, int friendId) {
        User user = getUser(id);
        User friend = getUser(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    @Override
    public List<User> getFriends(int id) {
        return getUser(id).getFriends().stream()
                .map(this::getUser)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int userId, int friendId) {
        Set<Integer> commonFriends = new HashSet<>(getUser(userId).getFriends());
        commonFriends.retainAll(getUser(friendId).getFriends());
        System.out.println(commonFriends);
        return commonFriends.stream()
                .map(this::getUser)
                .collect(Collectors.toList());
    }
}