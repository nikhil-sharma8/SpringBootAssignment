package com.zemoso.account.repository;

import com.zemoso.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserId(Long userId);
}
