package org.gardar.taskflow.dto;

public record TaskCreateRequest(
   String title,
   String description
) {}
