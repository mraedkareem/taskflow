package com.taskflow.controller;

import com.taskflow.dto.ChangePasswordRequest;
import com.taskflow.dto.UpdateEmailRequest;
import com.taskflow.dto.UserResponse;
import com.taskflow.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        return ResponseEntity.ok(UserResponse.from(userService.getCurrentUser()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateEmail(@RequestBody UpdateEmailRequest request) {
        return ResponseEntity.ok(UserResponse.from(userService.updateEmail(request.email())));
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request.currentPassword(), request.newPassword());
        return ResponseEntity.noContent().build();
    }
}
