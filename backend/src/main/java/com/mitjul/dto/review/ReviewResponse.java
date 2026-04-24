package com.mitjul.dto.review;

import com.mitjul.domain.review.Review;
import java.time.LocalDateTime;

public record ReviewResponse(
    Long id,
    Long bookId,
    String bookTitle,
    Byte rating,
    String oneLiner,
    String body,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
            review.getId(),
            review.getBook().getId(),
            review.getBook().getTitle(),
            review.getRating(),
            review.getOneLiner(),
            review.getBody(),
            review.getCreatedAt(),
            review.getUpdatedAt()
        );
    }
}
