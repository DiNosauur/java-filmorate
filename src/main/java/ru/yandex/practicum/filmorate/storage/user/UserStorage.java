package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> findAll();

    User createUser(User user);

    User updateUser(User user);

    User getUser(Long id);

    void addFriend(Long id, Long friendId);

    void delFriend(Long id, Long friendId);

    Collection<Long> getFriends(Long id);

}
