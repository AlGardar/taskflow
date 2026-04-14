package org.gardar.taskflow.dto;

import org.gardar.taskflow.model.TaskStatus;

public record TaskUpdateRequest(
        String title,
        String description,
        TaskStatus status
) {
}
