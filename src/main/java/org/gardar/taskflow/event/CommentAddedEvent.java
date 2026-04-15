package org.gardar.taskflow.event;

import java.time.Instant;

public record CommentAddedEvent(
        Long taskId,
        Long commentId,
        String text,
        Instant createdAt
) {
}
