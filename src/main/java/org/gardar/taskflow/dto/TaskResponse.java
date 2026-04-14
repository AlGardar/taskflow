package org.gardar.taskflow.dto;

import org.gardar.taskflow.model.Task;
import org.gardar.taskflow.model.TaskStatus;

import java.time.Instant;

public record TaskResponse(
   Long id,
   String title,
   String description,
   TaskStatus status,
   Instant createdAt
) {
    public static TaskResponse fromEntity(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt()
        );
    }
}
