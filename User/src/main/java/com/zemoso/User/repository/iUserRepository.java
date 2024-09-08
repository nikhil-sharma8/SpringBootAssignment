package com.zemoso.User.repository;

import com.zemoso.User.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface iUserRepository extends JpaRepository<User, Long> {
    User findByName(String name);
}
