package com.zemoso.User.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zemoso.User.model.User;
import com.zemoso.User.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegister() throws Exception {
        User user = new User();
        user.setName("John");
        user.setPassword("password");

        when(userService.saveUser(any(User.class))).thenReturn("User saved");

        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string("User saved"));

        verify(userService, times(1)).saveUser(any(User.class));
    }

    @Test
    void testUpdateBalance() throws Exception {
        when(userService.updateBalance(1L, 100.0, 1L)).thenReturn("Account Updated");

        mockMvc.perform(put("/api/v1/user/update/balance")
                        .param("userId", "1")
                        .param("amount", "100")
                        .param("accountId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Account Updated"));

        verify(userService, times(1)).updateBalance(1L, 100.0, 1L);
    }

    @Test
    void testDeleteUser() throws Exception {
        when(userService.deleteUser(1L)).thenReturn("User deleted");

        mockMvc.perform(delete("/api/v1/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted"));

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void testLogin() throws Exception {
        User user = new User();
        user.setName("John");
        user.setPassword("password");

        UserToken token = new UserToken();
        token.setToken("jwt-token");

        when(userService.verify(any(User.class))).thenReturn(token);

        mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));

        verify(userService, times(1)).verify(any(User.class));
    }
}
