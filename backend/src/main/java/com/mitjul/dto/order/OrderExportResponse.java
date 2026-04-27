package com.mitjul.dto.order;

import com.fasterxml.jackson.databind.JsonNode;
import com.mitjul.domain.order.BookOrder;
import com.mitjul.domain.order.OrderStatus;
import java.time.LocalDateTime;

public record OrderExportResponse(
    String orderNumber,
    OrderStatus status,
    LocalDateTime createdAt,
    LocalDateTime exportedAt,
    JsonNode snapshot
) {

    public static OrderExportResponse of(BookOrder order, LocalDateTime exportedAt, JsonNode snapshot) {
        return new OrderExportResponse(
            order.getOrderNumber(),
            order.getStatus(),
            order.getCreatedAt(),
            exportedAt,
            snapshot
        );
    }
}
