package com.mitjul.dto.dashboard;

import com.mitjul.domain.review.Review;
import java.time.LocalDateTime;

public record RecentReviewResponse(
    Long id,
    Long bookId,
    String bookTitle,
    Byte rating,
    String oneLiner,
    LocalDateTime createdAt
) {

    public static RecentReviewResponse from(Review review) {
        return new RecentReviewResponse(
            review.getId(),
            review.getBook().getId(),
            review.getBook().getTitle(),
            review.getRating(),
            review.getOneLiner(),
            review.getCreatedAt()
        );
    }
}
