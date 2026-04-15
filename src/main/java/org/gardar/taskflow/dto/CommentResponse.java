package org.gardar.taskflow.dto;

import org.gardar.taskflow.model.Comment;

import java.time.Instant;

public record CommentResponse(
        Long id,
        String text,
        Instant createdAt
) {
    public static CommentResponse fromEntity(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getText(),
                comment.getCreatedAt()
        );
    }
}
