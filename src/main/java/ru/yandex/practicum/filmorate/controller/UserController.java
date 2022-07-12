package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User put(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") Long id){
        return userService.getUser(id);
    }

    //PUT /users/{id}/friends/{friendId} — добавление в друзья.
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Long id, @PathVariable("friendId") Long friendId){
        userService.addFriend(id, friendId);
    }

    //DELETE /users/{id}/friends/{friendId} — удаление из друзей.
    @DeleteMapping("/{id}/friends/{friendId}")
    public void delFriend(@PathVariable("id") Long id, @PathVariable("friendId") Long friendId){
        userService.delFriend(id, friendId);
    }

    //GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable("id") Long id){
        return userService.getFriends(id);
    }

    //GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable("id") Long id, @PathVariable("otherId") Long otherId){
        return userService.getCommonFriends(id, otherId);
    }

}
