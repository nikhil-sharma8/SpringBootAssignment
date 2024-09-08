package com.zemoso.User.service;

import com.zemoso.User.model.MyUserDetails;
import com.zemoso.User.model.User;
import com.zemoso.User.repository.iUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private iUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByName(username);

        if (user==null){
            System.out.printf("User not found");
            throw new UsernameNotFoundException("User not found");
        }

        return new MyUserDetails(user);
    }
}
