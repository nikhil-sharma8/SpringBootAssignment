package com.zemoso.User.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class UserToken {

    private String username;
    private String token;
    private String expiration;
    private Boolean isAuthenticated;

    @Override
    public String toString() {
        return "UserToken {" +
                "\n  username: '" + username + '\'' +
                ",\n  token: '" + token + '\'' +
                ",\n  expiration: '" + expiration + '\'' +
                ",\n  isAuthenticated: " + isAuthenticated +
                "\n}";
    }
}
