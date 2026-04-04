package ru.ylab.levon.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.ylab.levon.dto.UserCreateDto;
import ru.ylab.levon.model.Role;
import ru.ylab.levon.model.User;
import ru.ylab.levon.service.api.UserService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Test
    void login_whenValidCredentials_returnsLoggedIn() throws Exception {
        when(userService.login("john", "pass")).thenReturn(true);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "john",
                                  "password": "pass"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("logged_in"));
    }

    @Test
    void login_whenInvalidCredentials_returnsInvalidCredentials() throws Exception {
        when(userService.login("john", "wrong")).thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "john",
                                  "password":"wrong"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid credentials"));
    }

    @Test
    void register_whenValidData_returnsCreated() throws Exception {
        when(userService.register(any(UserCreateDto.class))).thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "newuser",
                                  "password": "123456"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("created"));
    }

    @Test
    void register_whenUserExists_returnsConflict() throws Exception {
        when(userService.register(any(UserCreateDto.class))).thenReturn(false);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "existing",
                                  "password": "123456"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("User already exists"));
    }

    @Test
    void register_whenPasswordTooShort_returns400WithValidationError() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "newuser",
                                  "password": "pw"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.details[0]").value("Password must be at least 3 characters long"));
    }

    @Test
    void register_whenServiceThrowsIllegalArgumentException_returnsBadRequest() throws Exception {
        when(userService.register(any(UserCreateDto.class)))
                .thenThrow(new IllegalArgumentException("Bad data"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "usr",
                                  "password": "pwd"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad data"));
    }

    @Test
    void logout_whenUserLoggedIn_returnsOk() throws Exception {
        when(userService.getCurrentUser()).thenReturn(new User(1L, "john", "pass", Role.USER));

        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("logged_out"));

        verify(userService).logout();
    }

    @Test
    void logout_whenNoUserLoggedIn_returnsBadRequest() throws Exception {
        when(userService.getCurrentUser()).thenReturn(null);

        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("No user is logged in"));
    }
}
