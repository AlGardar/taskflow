package org.gardar.taskflow.service;

import org.gardar.taskflow.dto.*;
import org.gardar.taskflow.event.CommentAddedEvent;
import org.gardar.taskflow.model.Comment;
import org.gardar.taskflow.model.Task;
import org.gardar.taskflow.model.TaskStatus;
import org.gardar.taskflow.model.User;
import org.gardar.taskflow.repository.TaskRepository;
import org.gardar.taskflow.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, ApplicationEventPublisher eventPublisher, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.eventPublisher = eventPublisher;
        this.userRepository = userRepository;
    }

    @Cacheable(value = "tasks", key = "#authorId + '-' + #page + '-' + #size")
    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> getAllTasks(Long authorId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Task> taskPage = taskRepository.findAllByAuthorId(authorId, pageRequest);

        List<TaskResponse> content = taskPage.getContent().stream()
                .map(TaskResponse::fromEntity)
                .toList();

        return new PageResponse<>(
                content,
                taskPage.getNumber(),
                taskPage.getSize(),
                taskPage.getTotalElements(),
                taskPage.getTotalPages()
        );
    }

    @CacheEvict(value = "tasks", allEntries = true)
    @Transactional(readOnly = true)
    public TaskDetailResponse getTaskWithComments(Long id, Long authorId) {
        Task detailTask = taskRepository.findByIdAndAuthorIdWithComments(id, authorId)
                .orElseThrow(() -> new IllegalArgumentException("Task with id: " + id + " not found."));
        return TaskDetailResponse.fromEntity(detailTask);
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task with id: " + id + " not found."));
        return TaskResponse.fromEntity(task);
    }

    @CacheEvict(value = "tasks", allEntries = true)
    @Transactional
    public TaskResponse createTask(TaskCreateRequest request, Long authorId) {
        User authorReference = userRepository.getReferenceById(authorId);

        Task task = new Task(
                request.title(),
                request.description(),
                TaskStatus.TODO,
                authorReference
                );

        Task savedTask = taskRepository.save(task);
        return TaskResponse.fromEntity(savedTask);
    }

    @CacheEvict(value = "tasks", allEntries = true)
    @Transactional
    public TaskResponse updatedTask(Long id, Long authorId, TaskUpdateRequest request) {
        Task task = taskRepository.findByIdAndAuthorId(id, authorId)
                .orElseThrow(() -> new IllegalArgumentException("Task with id: " + id + " not found."));
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());

        return TaskResponse.fromEntity(task);
    }

    @CacheEvict(value = "tasks", allEntries = true)
    @Transactional
    public void deleteTask(Long id, Long authorId) {
        if (!taskRepository.existsByIdAndAuthorId(id, authorId)) {
            throw new IllegalArgumentException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    @CacheEvict(value = "tasks", allEntries = true)
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
