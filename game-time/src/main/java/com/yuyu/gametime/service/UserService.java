package com.yuyu.gametime.service;

import com.yuyu.gametime.dto.LoginRequest;
import com.yuyu.gametime.dto.RegisterRequest;
import com.yuyu.gametime.dto.UpdateProfileRequest;
import com.yuyu.gametime.entity.User;
import com.yuyu.gametime.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public User register(RegisterRequest request) {
        if (userMapper.countByUsername(request.getUsername()) > 0) {
            throw new IllegalArgumentException("用户名已存在");
        }
        if (request.getEmail() != null && userMapper.countByEmail(request.getEmail()) > 0) {
            throw new IllegalArgumentException("邮箱已被占用");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userMapper.insert(user);
        return user;
    }

    @Transactional(readOnly = true)
    public User login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return user;
    }

    @Transactional
    public User updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        userMapper.update(user);
        return user;
    }
}
