package org.gardar.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.gardar.taskflow.model.TaskStatus;

public record TaskUpdateRequest(
        @NotBlank(message = "Title cannot be empty.")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,
        @Size(max = 2000, message = "Description is too long")
        String description,
        @NotNull(message = "Status cannot be null")
        TaskStatus status
) {
}
