package com.mitjul.dto.order;

import com.mitjul.domain.quote.QuoteCard;
import com.mitjul.domain.tag.Tag;
import java.time.LocalDateTime;
import java.util.List;

public record OrderQuoteSnapshotResponse(
    Long id,
    Integer page,
    String content,
    String memo,
    List<String> tags,
    LocalDateTime createdAt
) {

    public static OrderQuoteSnapshotResponse from(QuoteCard quoteCard) {
        return new OrderQuoteSnapshotResponse(
            quoteCard.getId(),
            quoteCard.getPage(),
            quoteCard.getContent(),
            quoteCard.getMemo(),
            quoteCard.getTags().stream()
                .map(Tag::getName)
                .sorted()
                .toList(),
            quoteCard.getCreatedAt()
        );
    }
}
