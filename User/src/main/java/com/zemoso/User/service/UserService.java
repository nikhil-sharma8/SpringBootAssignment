package com.zemoso.User.service;

import com.zemoso.User.dto.UserToken;
import com.zemoso.User.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    String saveUser(User user);

    User getUserById(Long id);

    String deleteUser(Long id);


    UserToken verify(User user);

    String updateBalance(Long userId, Double amount, Long accountId);
}
