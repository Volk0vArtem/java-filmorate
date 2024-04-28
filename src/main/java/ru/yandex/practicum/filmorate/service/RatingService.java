package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingStorage storage;
    public List<Rating> getRatings(){
        return storage.getRatings();
    }

    public Rating getRating(int id){
        return storage.getRating(id);
    }
}
