package com.zemoso.user.controller;

import com.zemoso.user.dto.UserToken;
import com.zemoso.user.model.User;
import com.zemoso.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.*;

class UserControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    UserController userController;

    @Mock
    UserService userService;

    User user = new User();
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        user.setName("testUser");
        user.setPassword("password");
    }


    @Test
    void testRegister() throws Exception {
        when(userService.saveUser(any(User.class))).thenReturn("User saved");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User saved"));
    }

    @Test
    void testGetAllUser() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(user));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("testUser"));
    }

    @Test
    void testGetUserById() throws Exception {

        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("testUser"));
    }

    @Test
    void testDeleteUser() throws Exception {
        when(userService.deleteUser(1L)).thenReturn("User deleted");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/user/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User deleted"));
    }

    @Test
    void testUpdateBalance() throws Exception {
        when(userService.updateBalance(1L, 100.0, 1L)).thenReturn("Account Updated");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user/update/balance")
                        .param("userId", "1")
                        .param("amount", "100.0")
                        .param("accountId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Account Updated"));
    }

    @Test
    void testLogin() throws Exception {
        UserToken userToken = new UserToken();
        userToken.setToken("jwtToken");
        userToken.setIsAuthenticated(true);
        userToken.setExpiration("30 minutes");

        when(userService.verify(any(User.class))).thenReturn(userToken);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("jwtToken"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isAuthenticated").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.expiration").value("30 minutes"));
    }

}
