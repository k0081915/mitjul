package com.mitjul.dto.order;

import com.mitjul.domain.order.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequest(
    @NotNull(message = "주문 상태는 필수입니다.")
    OrderStatus status
) {
}
