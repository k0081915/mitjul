package com.mitjul.api;

import com.mitjul.dto.review.ReviewRequest;
import com.mitjul.dto.review.ReviewResponse;
import com.mitjul.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books/{bookId}/review")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ReviewResponse getReview(@PathVariable Long bookId) {
        return reviewService.getReview(bookId);
    }

    @PutMapping
    public ReviewResponse upsertReview(
        @PathVariable Long bookId,
        @Valid @RequestBody ReviewRequest request
    ) {
        return reviewService.upsertReview(bookId, request);
    }
}
