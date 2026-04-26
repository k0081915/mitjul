package com.mitjul.dto.order;

import com.mitjul.domain.order.CoverStyle;
import java.time.LocalDate;
import java.util.List;

public record OrderPreviewResponse(
    LocalDate periodStart,
    LocalDate periodEnd,
    CoverStyle coverStyle,
    String ownerName,
    long bookCount,
    long quoteCount,
    List<OrderBookPreviewResponse> books
) {
}
