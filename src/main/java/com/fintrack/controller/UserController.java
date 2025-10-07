package com.fintrack.controller;

import com.fintrack.dto.response.UserResponse;
import com.fintrack.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        log.info("GET /api/users/me - Get current user");
        UserResponse response = userService.getCurrentUser();
        return ResponseEntity.ok(response);
    }
}

