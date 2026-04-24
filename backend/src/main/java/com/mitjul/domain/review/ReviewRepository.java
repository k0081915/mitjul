package com.mitjul.domain.review;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByBookId(Long bookId);

    boolean existsByBookId(Long bookId);
}
