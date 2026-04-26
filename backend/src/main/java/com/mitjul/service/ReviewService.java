package com.mitjul.service;

import com.mitjul.common.error.ApiException;
import com.mitjul.common.error.ErrorCode;
import com.mitjul.domain.book.Book;
import com.mitjul.domain.book.BookRepository;
import com.mitjul.domain.book.BookStatus;
import com.mitjul.domain.review.Review;
import com.mitjul.domain.review.ReviewRepository;
import com.mitjul.dto.review.ReviewRequest;
import com.mitjul.dto.review.ReviewResponse;
import java.time.Clock;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private static final Long SEED_USER_ID = 1L;

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final Clock clock;

    public ReviewResponse getReview(Long bookId) {
        ensureBookExists(bookId);
        return reviewRepository.findByBookId(bookId)
            .map(ReviewResponse::from)
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "리뷰를 찾을 수 없습니다."));
    }

    @Transactional
    public ReviewResponse upsertReview(Long bookId, ReviewRequest request) {
        Book book = ensureBookExists(bookId);

        Review review = reviewRepository.findByBookId(bookId)
            .map(existingReview -> {
                existingReview.update(request.rating(), request.oneLiner(), request.body());
                return existingReview;
            })
            .orElseGet(() -> reviewRepository.save(
                Review.create(book, request.rating(), request.oneLiner(), request.body())
            ));

        if (Boolean.TRUE.equals(request.markCompleted())) {
            book.update(null, null, null, null, BookStatus.COMPLETED, null, resolveFinishedAt(book));
        }

        return ReviewResponse.from(review);
    }

    private LocalDate resolveFinishedAt(Book book) {
        return book.getFinishedAt() == null ? LocalDate.now(clock) : book.getFinishedAt();
    }

    private Book ensureBookExists(Long bookId) {
        return bookRepository.findById(bookId)
            .filter(book -> book.getUser().getId().equals(SEED_USER_ID))
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "책을 찾을 수 없습니다."));
    }
}
