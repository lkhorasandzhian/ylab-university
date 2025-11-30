package ru.ylab.levon.controller;

import java.util.Map;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ylab.levon.dto.UserCreateDto;
import ru.ylab.levon.dto.UserLoginDto;
import ru.ylab.levon.service.api.UserService;

/**
 * REST-контроллер, реализующий операции аутентификации и регистрации пользователей.
 * <p>
 * Поддерживаемые маршруты:
 * <ul>
 *     <li><b>POST /api/auth/login</b> — вход пользователя</li>
 *     <li><b>POST /api/auth/register</b> — регистрация пользователя</li>
 *     <li><b>POST /api/auth/logout</b> — выход из системы</li>
 * </ul>
 * Все запросы принимают и возвращают JSON.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    /**
     * Обрабатывает вход пользователя.
     *
     * @param dto DTO с логином и паролем
     * @return Статус:
     * <ul>
     *     <li>200 OK — успешный вход</li>
     *     <li>400 BAD REQUEST — DTO не прошло валидацию</li>
     *     <li>401 UNAUTHORIZED — неверные учётные данные</li>
     * </ul>
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDto dto) {
        boolean ok = userService.login(dto.username(), dto.password());

        if (!ok) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }

        return ResponseEntity.ok(Map.of("status", "logged_in"));
    }

    /**
     * Обрабатывает регистрацию нового пользователя.
     *
     * @param dto DTO данных пользователя
     * @return Статус:
     * <ul>
     *     <li>200 OK — успешная регистрация</li>
     *     <li>400 BAD REQUEST — DTO не прошло валидацию</li>
     *     <li>409 CONFLICT — пользователь уже существует</li>
     * </ul>
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserCreateDto dto) {
        boolean ok;
        try {
            ok = userService.register(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }

        if (!ok) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "User already exists"));
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("status", "created"));
    }

    /**
     * Обрабатывает выход текущего авторизованного пользователя.
     *
     * @return Статус:
     * <ul>
     *     <li>200 OK — успешный выход</li>
     *     <li>400 BAD REQUEST — пользователь и так не авторизован</li>
     * </ul>
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        if (userService.getCurrentUser() == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "No user is logged in"));
        }

        userService.logout();
        return ResponseEntity.ok(Map.of("status", "logged_out"));
    }
}
