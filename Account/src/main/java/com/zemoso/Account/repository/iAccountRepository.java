package com.zemoso.Account.repository;

import com.zemoso.Account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface iAccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserId(Long userId);
}
