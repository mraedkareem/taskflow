package com.taskflow.service;

import com.taskflow.model.Task;
import com.taskflow.repository.TaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void logInAs() {
        // TaskService reads the current user from the SecurityContext,
        // so tests put a fake logged-in user there.
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("alice", null, Collections.emptyList())
        );
    }

    @AfterEach
    void logOut() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void create_assignsCurrentUserAsOwner() {
        Task input = new Task();
        input.setTitle("Write tests");

        when(taskRepository.save(any(Task.class))).thenAnswer(call -> call.getArgument(0));

        Task saved = taskService.create(input);

        assertEquals("alice", saved.getOwnerUsername());
    }

    @Test
    void getOne_refusesAccessToAnotherUsersTask() {
        Task bobsTask = new Task();
        bobsTask.setOwnerUsername("bob");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(bobsTask));

        RuntimeException error = assertThrows(RuntimeException.class, () -> taskService.getOne(1L));

        assertEquals("Not your task", error.getMessage());
    }

    @Test
    void delete_refusesToDeleteAnotherUsersTask() {
        Task bobsTask = new Task();
        bobsTask.setOwnerUsername("bob");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(bobsTask));

        assertThrows(RuntimeException.class, () -> taskService.delete(1L));
        verify(taskRepository, never()).delete(any());
    }
}
