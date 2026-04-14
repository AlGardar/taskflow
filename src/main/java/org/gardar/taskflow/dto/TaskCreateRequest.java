package org.gardar.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskCreateRequest(
   @NotBlank(message = "Title cannot be empty.")
   @Size(max = 255, message = "Title must not exceed 255 characters")
   String title,
   @Size(max = 2000, message = "Description is too long")
   String description
) {}
