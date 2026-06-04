package com.yuyu.gametime.controller;

import com.yuyu.gametime.dto.ApiResponse;
import com.yuyu.gametime.dto.UpdateProfileRequest;
import com.yuyu.gametime.entity.User;
import com.yuyu.gametime.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取当前登录用户个人信息
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("未登录", 401));
        }
        User user = userService.getUserById(userId);
        Map<String, Object> profile = new HashMap<>();
        profile.put("userId", user.getId());
        profile.put("username", user.getUsername());
        profile.put("email", user.getEmail());
        profile.put("phone", user.getPhone());
        profile.put("avatar", user.getAvatar());
        return ResponseEntity.ok(ApiResponse.ok(profile));
    }

    /**
     * 更新当前登录用户个人信息
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("未登录", 401));
        }
        User user = userService.updateProfile(userId, request);
        Map<String, Object> profile = new HashMap<>();
        profile.put("userId", user.getId());
        profile.put("username", user.getUsername());
        profile.put("email", user.getEmail());
        profile.put("phone", user.getPhone());
        profile.put("avatar", user.getAvatar());
        return ResponseEntity.ok(ApiResponse.ok(profile));
    }
}
