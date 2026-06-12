package com.taskflow.service;

import com.taskflow.dto.UserResponse;
import com.taskflow.model.Task;
import com.taskflow.model.User;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public AdminService(UserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(UserResponse::from)
            .toList();
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

        String me = SecurityContextHolder.getContext().getAuthentication().getName();
        if (user.getUsername().equals(me)) {
            throw new RuntimeException("You cannot delete your own account");
        }

        // Remove the user's tasks first so no orphaned rows are left behind.
        taskRepository.deleteByOwnerUsername(user.getUsername());
        userRepository.delete(user);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepository.delete(task);
    }
}
