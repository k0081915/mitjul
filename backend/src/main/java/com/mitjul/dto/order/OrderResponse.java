package com.mitjul.dto.order;

import com.mitjul.domain.order.BookOrder;
import com.mitjul.domain.order.CoverStyle;
import com.mitjul.domain.order.OrderItem;
import com.mitjul.domain.order.OrderStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
    Long id,
    String orderNumber,
    LocalDate periodStart,
    LocalDate periodEnd,
    CoverStyle coverStyle,
    String ownerName,
    OrderStatus status,
    long bookCount,
    long quoteCount,
    List<OrderItemResponse> items,
    String snapshotJson,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static OrderResponse from(BookOrder order, List<OrderItem> items) {
        long quoteCount = items.stream()
            .mapToLong(OrderItem::getQuoteCount)
            .sum();

        return new OrderResponse(
            order.getId(),
            order.getOrderNumber(),
            order.getPeriodStart(),
            order.getPeriodEnd(),
            order.getCoverStyle(),
            order.getOwnerName(),
            order.getStatus(),
            items.size(),
            quoteCount,
            items.stream().map(OrderItemResponse::from).toList(),
            order.getSnapshotJson(),
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }
}
