package org.gardar.taskflow.service;

import org.gardar.taskflow.dto.TaskCreateRequest;
import org.gardar.taskflow.dto.TaskResponse;
import org.gardar.taskflow.dto.TaskUpdateRequest;
import org.gardar.taskflow.model.Task;
import org.gardar.taskflow.model.TaskStatus;
import org.gardar.taskflow.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task with id: " + id + " not found."));
        return TaskResponse.fromEntity(task);
    }

    @Transactional
    public TaskResponse createTask(TaskCreateRequest request) {
        Task task = new Task(
                request.title(),
                request.description(),
                TaskStatus.TODO
        );

        Task savedTask = taskRepository.save(task);
        return TaskResponse.fromEntity(savedTask);
    }

    @Transactional
    public TaskResponse updatedTask(Long id, TaskUpdateRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task with id: " + id + " not found."));
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());

        return TaskResponse.fromEntity(task);
    }

    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new IllegalArgumentException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }
}
