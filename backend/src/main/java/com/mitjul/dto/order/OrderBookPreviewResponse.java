package com.mitjul.dto.order;

import com.mitjul.domain.book.Book;
import java.util.List;

public record OrderBookPreviewResponse(
    Long bookId,
    String title,
    String author,
    Long quoteCount,
    List<OrderQuoteSnapshotResponse> quotes
) {

    public static OrderBookPreviewResponse of(
        Book book,
        Long quoteCount,
        List<OrderQuoteSnapshotResponse> quotes
    ) {
        return new OrderBookPreviewResponse(
            book.getId(),
            book.getTitle(),
            book.getAuthor(),
            quoteCount,
            quotes
        );
    }
}
