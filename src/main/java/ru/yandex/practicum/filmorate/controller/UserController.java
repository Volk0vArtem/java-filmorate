package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();

    private int idGenerator = 1;
    @PostMapping
    public User postUser( @RequestBody User user) {
        user.setId(idGenerator++);
        if (user.getName() == null){
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User putUser( @RequestBody User user) {
        users.put(user.getId(), user);
        return user;
    }

    @GetMapping
    public List<User> getUsers(){
        return new ArrayList<>(users.values());
    }

}
