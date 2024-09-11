package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.yandex.practicum.filmorate.annotation.NotFutureBirthday;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class User {
    private Long id;
    @Email
    @NotBlank
    private String email;
    private String name;
    @NotBlank
    private String login;
    @NotFutureBirthday
    private String birthday;
    private List<Long> friends = new ArrayList<>();
}