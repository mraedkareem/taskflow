package com.taskflow.service;

import com.taskflow.dto.TaskStats;
import com.taskflow.model.Task;
import com.taskflow.repository.TaskRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    private String currentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public Task create(Task task) {
        task.setId(null);
        task.setOwnerUsername(currentUsername());
        return taskRepository.save(task);
    }

    public List<Task> getMyTasks() {
        return taskRepository.findByOwnerUsername(currentUsername());
    }

    public Task getOne(Long id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        if (!task.getOwnerUsername().equals(currentUsername())) {
            throw new RuntimeException("Not your task");
        }
        return task;
    }

    public Task update(Long id, Task updated) {
        Task task = getOne(id); // also checks ownership
        task.setTitle(updated.getTitle());
        task.setDescription(updated.getDescription());
        if (updated.getStatus() != null) {
            task.setStatus(updated.getStatus());
        }
        return taskRepository.save(task);
    }

    public void delete(Long id) {
        Task task = getOne(id); // also checks ownership
        taskRepository.delete(task);
    }

    public List<Task> getByStatus(Task.Status status) {
        return taskRepository.findByOwnerUsernameAndStatus(currentUsername(), status);
    }

    public Task updateStatus(Long id, Task.Status status) {
        Task task = getOne(id); // also checks ownership
        task.setStatus(status);
        return taskRepository.save(task);
    }

    public TaskStats getStats() {
        String me = currentUsername();
        long todo = taskRepository.countByOwnerUsernameAndStatus(me, Task.Status.TODO);
        long inProgress = taskRepository.countByOwnerUsernameAndStatus(me, Task.Status.IN_PROGRESS);
        long done = taskRepository.countByOwnerUsernameAndStatus(me, Task.Status.DONE);
        return new TaskStats(todo, inProgress, done, todo + inProgress + done);
    }
}
