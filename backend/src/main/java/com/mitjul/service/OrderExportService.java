package com.mitjul.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitjul.common.error.ApiException;
import com.mitjul.common.error.ErrorCode;
import com.mitjul.domain.order.BookOrder;
import com.mitjul.domain.order.BookOrderRepository;
import com.mitjul.dto.order.OrderExportResponse;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderExportService {

    private static final Long SEED_USER_ID = 1L;

    private final BookOrderRepository bookOrderRepository;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    public OrderExportResponse exportJson(Long orderId) {
        BookOrder order = bookOrderRepository.findByIdAndUserId(orderId, SEED_USER_ID)
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "주문을 찾을 수 없습니다."));

        return OrderExportResponse.of(
            order,
            LocalDateTime.now(clock),
            readSnapshot(order)
        );
    }

    private JsonNode readSnapshot(BookOrder order) {
        if (order.getSnapshotJson() == null || order.getSnapshotJson().isBlank()) {
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "주문 스냅샷을 읽을 수 없습니다.");
        }

        try {
            return objectMapper.readTree(order.getSnapshotJson());
        } catch (JsonProcessingException exception) {
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "주문 스냅샷을 읽을 수 없습니다.");
        }
    }
}
