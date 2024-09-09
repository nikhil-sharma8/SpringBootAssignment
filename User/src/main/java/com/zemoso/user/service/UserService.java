package com.zemoso.user.service;

import com.zemoso.user.dto.UserToken;
import com.zemoso.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    String saveUser(User user);

    User getUserById(Long id);

    String deleteUser(Long id);


    UserToken verify(User user);

    String updateBalance(Long userId, Double amount, Long accountId);
}
