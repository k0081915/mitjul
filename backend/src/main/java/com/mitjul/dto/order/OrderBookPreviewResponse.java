package com.mitjul.dto.order;

import com.mitjul.domain.book.Book;

public record OrderBookPreviewResponse(
    Long bookId,
    String title,
    String author,
    Long quoteCount
) {

    public static OrderBookPreviewResponse of(Book book, Long quoteCount) {
        return new OrderBookPreviewResponse(
            book.getId(),
            book.getTitle(),
            book.getAuthor(),
            quoteCount
        );
    }
}
