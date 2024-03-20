package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();

    private int idGenerator = 1;

    @PostMapping
    public User postUser(@Valid @RequestBody User user) {
        user.setId(idGenerator++);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Добавлен пользователь id={}", user.getId());
        return user;
    }

    @PutMapping
    public User putUser(@Valid @RequestBody User user) throws ValidationException {
        if (!users.containsKey(user.getId())) {
            log.info("Попытка изменения несуществующего пользователя id={}", user.getId());
            throw new ValidationException("Такого пользователя не существует");

        }
        users.put(user.getId(), user);
        log.info("Изменён пользователь id={}", user.getId());
        return user;
    }

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

}
