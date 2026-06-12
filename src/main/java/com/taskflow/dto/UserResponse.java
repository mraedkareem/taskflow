package com.taskflow.dto;

import com.taskflow.model.User;

/**
 * What the API returns about a user — deliberately excludes the password.
 */
public record UserResponse(Long id, String username, String email, User.Role role) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }
}
