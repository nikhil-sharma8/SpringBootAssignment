package com.zemoso.User.controller;

import com.zemoso.User.dto.UserToken;
import com.zemoso.User.model.User;
import com.zemoso.User.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public String register(@RequestBody User user){
        return userService.saveUser(user);
    }

    @PostMapping("/login")
    public UserToken login(@RequestBody User user){
        return userService.verify(user);
    }

    @GetMapping
    public List<User> getAllUser(){
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id){
        return userService.getUserById(id);
    }


    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id){
        return userService.deleteUser(id);
    }

    @PutMapping("/update/balance")
    public String updateBalance(@RequestParam Long userId, @RequestParam Double amount, @RequestParam Long accountId){
        return userService.updateBalance(userId, amount, accountId);
    }
}
