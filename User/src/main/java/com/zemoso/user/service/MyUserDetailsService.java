package com.zemoso.user.service;

import com.zemoso.user.model.MyUserDetails;
import com.zemoso.user.model.User;
import com.zemoso.user.repository.IUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    IUserRepository userRepository;

    MyUserDetailsService(IUserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByName(username);

        if (user==null){
            throw new UsernameNotFoundException("User not found");
        }

        return new MyUserDetails(user);
    }
}
