package com.mitjul.dto.book;

import com.mitjul.domain.book.Book;
import com.mitjul.domain.book.BookStatus;
import java.time.LocalDate;

public record BookResponse(
    Long id,
    String title,
    String author,
    String coverImageUrl,
    String presetCoverKey,
    BookStatus status,
    LocalDate startedAt,
    LocalDate finishedAt
) {

    public static BookResponse from(Book book) {
        return new BookResponse(
            book.getId(),
            book.getTitle(),
            book.getAuthor(),
            book.getCoverImageUrl(),
            book.getPresetCoverKey(),
            book.getStatus(),
            book.getStartedAt(),
            book.getFinishedAt()
        );
    }
}
