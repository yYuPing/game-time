package com.yuyu.game_time.controller;

import com.yuyu.game_time.dto.ApiResponse;
import com.yuyu.game_time.dto.AuthResponse;
import com.yuyu.game_time.dto.LoginRequest;
import com.yuyu.game_time.dto.RegisterRequest;
import com.yuyu.game_time.entity.User;
import com.yuyu.game_time.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, HttpSession session) {
        User user = userService.register(request);
        // 注册后自动登录，存入 session
        session.setAttribute("userId", user.getId());
        AuthResponse response = new AuthResponse(true, "注册成功", user.getId(), user.getUsername(),
                user.getEmail(), user.getPhone(), user.getAvatar());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        User user = userService.login(request);
        // 登录信息存入 Redis session
        session.setAttribute("userId", user.getId());
        AuthResponse response = new AuthResponse(true, "登录成功", user.getId(), user.getUsername(),
                user.getEmail(), user.getPhone(), user.getAvatar());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.okMsg("已退出登录", null));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("未登录", 401));
        }
        User user = userService.getUserById(userId);
        AuthResponse response = new AuthResponse(true, null, user.getId(), user.getUsername(),
                user.getEmail(), user.getPhone(), user.getAvatar());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
