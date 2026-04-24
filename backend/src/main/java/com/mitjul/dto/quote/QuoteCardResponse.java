package com.mitjul.dto.quote;

import com.mitjul.domain.quote.QuoteCard;
import java.time.LocalDateTime;
import java.util.List;

public record QuoteCardResponse(
    Long id,
    Long bookId,
    String bookTitle,
    Integer page,
    String content,
    String memo,
    List<String> tags,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static QuoteCardResponse from(QuoteCard quoteCard) {
        return new QuoteCardResponse(
            quoteCard.getId(),
            quoteCard.getBook().getId(),
            quoteCard.getBook().getTitle(),
            quoteCard.getPage(),
            quoteCard.getContent(),
            quoteCard.getMemo(),
            quoteCard.getTags().stream()
                .map(tag -> tag.getName())
                .sorted()
                .toList(),
            quoteCard.getCreatedAt(),
            quoteCard.getUpdatedAt()
        );
    }
}
