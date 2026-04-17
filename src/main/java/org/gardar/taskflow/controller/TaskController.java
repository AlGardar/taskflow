package org.gardar.taskflow.controller;

import jakarta.validation.Valid;
import org.gardar.taskflow.dto.*;
import org.gardar.taskflow.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public PageResponse<TaskResponse> getAllTasks(@AuthenticationPrincipal Jwt jwt, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Long userId = jwt.getClaim("userId");
        return taskService.getAllTasks(userId, page, size);
    }

    @GetMapping("/{id}")
    public TaskDetailResponse getTaskById(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return taskService.getTaskWithComments(id, jwt.getClaim("userId"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(@Valid @RequestBody TaskCreateRequest request, @AuthenticationPrincipal Jwt jwt) {
        return taskService.createTask(request, jwt.getClaim("userId"));
    }

    @PutMapping("/{id}")
    public TaskResponse updateTask(@PathVariable Long id,
                                   @Valid @RequestBody TaskUpdateRequest request,
                                   @AuthenticationPrincipal Jwt jwt) {
        return taskService.updateTask(id, jwt.getClaim("userId"), request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        taskService.deleteTask(id, jwt.getClaim("userId"));
    }

    @PostMapping("/{id}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse addComment(@PathVariable Long id,
                                      @Valid @RequestBody CommentCreatedRequest request,
                                      @AuthenticationPrincipal Jwt jwt) {
        return taskService.addCommentToTask(id, jwt.getClaim("userId"), request);
    }
}
