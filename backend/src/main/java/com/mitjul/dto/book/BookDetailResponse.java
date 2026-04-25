package com.mitjul.dto.book;

import com.mitjul.domain.book.Book;
import com.mitjul.domain.book.BookStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookDetailResponse(
    Long id,
    String title,
    String author,
    String coverImageUrl,
    String presetCoverKey,
    BookStatus status,
    LocalDate startedAt,
    LocalDate finishedAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static BookDetailResponse from(Book book) {
        return new BookDetailResponse(
            book.getId(),
            book.getTitle(),
            book.getAuthor(),
            book.getCoverImageUrl(),
            book.getPresetCoverKey(),
            book.getStatus(),
            book.getStartedAt(),
            book.getFinishedAt(),
            book.getCreatedAt(),
            book.getUpdatedAt()
        );
    }
}
