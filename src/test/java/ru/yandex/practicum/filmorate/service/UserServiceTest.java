package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.inMemory.InMemoryUserStorage;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService service;
    User user1;
    User user2;
    User user3;

    @BeforeEach
    public void beforeEach() {
        service = new UserService(new InMemoryUserStorage());
        user1 = new User();
        user2 = new User();
        user3 = new User();
        service.addUser(user1);
        service.addUser(user2);
        service.addUser(user3);
    }

    @Test
    void addToFriends() {
        service.addToFriends(1, 2);
        assertEquals(1, service.getUser(1).getFriends().size());
        assertTrue(service.getUser(1).getFriends().contains(2));
        assertEquals(1, service.getUser(2).getFriends().size());
        assertTrue(service.getUser(2).getFriends().contains(1));
        assertThrows(NotFoundException.class, () -> service.addToFriends(1, 4));
    }

    @Test
    void removeFromFriends() {
        service.addToFriends(1, 2);
        service.removeFromFriends(1, 2);
        assertEquals(0, user1.getFriends().size());
        assertEquals(0, user2.getFriends().size());
        assertThrows(NotFoundException.class, () -> service.removeFromFriends(1, 4));
    }

    @Test
    void getFriends() {
        service.addToFriends(1, 2);
        service.addToFriends(1, 3);
        assertEquals(2, user1.getFriends().size());
        assertTrue(user1.getFriends().contains(2));
        assertTrue(user1.getFriends().contains(3));
    }

    @Test
    void getCommonFriends() {
        assertEquals(0, service.getCommonFriends(2, 3).size());
        service.addToFriends(1, 2);
        service.addToFriends(1, 3);
        assertEquals(1, service.getCommonFriends(2, 3).size());
        assertTrue(service.getCommonFriends(2, 3).contains(user1));
        assertThrows(NotFoundException.class, () -> service.getCommonFriends(1, 4));
    }
}