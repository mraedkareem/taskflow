package com.taskflow.dto;

import com.taskflow.model.Task;

public record UpdateStatusRequest(Task.Status status) {
}
