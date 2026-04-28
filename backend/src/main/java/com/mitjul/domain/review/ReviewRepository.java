package com.mitjul.domain.review;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByBookId(Long bookId);

    boolean existsByBookId(Long bookId);

    long countByBookUserId(Long userId);

    long countByBookUserIdAndCreatedAtBetween(
        Long userId,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
    );

    List<Review> findTop5ByBookUserIdOrderByCreatedAtDesc(Long userId);
}
