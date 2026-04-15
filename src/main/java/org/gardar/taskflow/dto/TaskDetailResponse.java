package org.gardar.taskflow.dto;

import org.gardar.taskflow.model.Task;
import org.gardar.taskflow.model.TaskStatus;

import java.time.Instant;
import java.util.List;

public record TaskDetailResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        Instant createdAt,
        List<CommentResponse> comments
) {
    public static TaskDetailResponse fromEntity(Task task) {
        return new TaskDetailResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getComments().stream()
                        .map(CommentResponse::fromEntity)
                        .toList()
        );
    }
}
