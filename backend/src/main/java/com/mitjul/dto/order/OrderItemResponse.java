package com.mitjul.dto.order;

import com.mitjul.domain.order.OrderItem;

public record OrderItemResponse(
    Long bookId,
    String title,
    String author,
    Integer quoteCount,
    Integer displayOrder
) {

    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
            item.getBook().getId(),
            item.getBook().getTitle(),
            item.getBook().getAuthor(),
            item.getQuoteCount(),
            item.getDisplayOrder()
        );
    }
}
