package com.mitjul.dto.order;

import com.mitjul.domain.order.BookOrder;
import com.mitjul.domain.order.CoverStyle;
import com.mitjul.domain.order.OrderItem;
import com.mitjul.domain.order.OrderStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record OrderSummaryResponse(
    Long id,
    String orderNumber,
    LocalDate periodStart,
    LocalDate periodEnd,
    CoverStyle coverStyle,
    String ownerName,
    OrderStatus status,
    long bookCount,
    long quoteCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static OrderSummaryResponse from(BookOrder order, List<OrderItem> items) {
        return new OrderSummaryResponse(
            order.getId(),
            order.getOrderNumber(),
            order.getPeriodStart(),
            order.getPeriodEnd(),
            order.getCoverStyle(),
            order.getOwnerName(),
            order.getStatus(),
            items.size(),
            items.stream().mapToLong(OrderItem::getQuoteCount).sum(),
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }
}
