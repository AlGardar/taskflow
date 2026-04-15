package org.gardar.taskflow.service;

import org.gardar.taskflow.dto.*;
import org.gardar.taskflow.event.CommentAddedEvent;
import org.gardar.taskflow.model.Comment;
import org.gardar.taskflow.model.Task;
import org.gardar.taskflow.model.TaskStatus;
import org.gardar.taskflow.repository.TaskRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TaskService(TaskRepository taskRepository, ApplicationEventPublisher eventPublisher) {
        this.taskRepository = taskRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskDetailResponse getTaskWithComment(Long id) {
        Task detailTask = taskRepository.findByIdWithComments(id)
                .orElseThrow(() -> new IllegalArgumentException("Task with id: " + id + " not found."));
        return TaskDetailResponse.fromEntity(detailTask);
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

    @Transactional
    public CommentResponse addCommentToTask(Long taskId, CommentCreatedRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task with id: " + taskId + " not found."));

        Comment comment = new Comment(request.text(), task);
        task.addComment(comment);

        taskRepository.flush();

        eventPublisher.publishEvent(new CommentAddedEvent(
                task.getId(),
                comment.getId(),
                comment.getText(),
                comment.getCreatedAt()
        ));
        return CommentResponse.fromEntity(comment);
    }
}
