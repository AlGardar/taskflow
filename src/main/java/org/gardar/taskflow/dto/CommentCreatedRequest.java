package org.gardar.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentCreatedRequest(
        @NotBlank(message = "Comment text cannot be empty.")
        @Size(max = 2000, message = "Comment is too long")
        String text
) {
}
