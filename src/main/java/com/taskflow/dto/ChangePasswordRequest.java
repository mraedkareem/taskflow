package com.taskflow.dto;

public record ChangePasswordRequest(String currentPassword, String newPassword) {
}
