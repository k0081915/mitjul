package com.mitjul.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitjul.common.error.ApiException;
import com.mitjul.common.error.ErrorCode;
import com.mitjul.domain.book.Book;
import com.mitjul.domain.book.BookRepository;
import com.mitjul.domain.order.BookOrder;
import com.mitjul.domain.order.BookOrderRepository;
import com.mitjul.domain.order.OrderItem;
import com.mitjul.domain.order.OrderItemRepository;
import com.mitjul.domain.quote.QuoteCardRepository;
import com.mitjul.domain.user.User;
import com.mitjul.domain.user.UserRepository;
import com.mitjul.dto.order.OrderBookPreviewResponse;
import com.mitjul.dto.order.OrderPreviewResponse;
import com.mitjul.dto.order.OrderRequest;
import com.mitjul.dto.order.OrderResponse;
import com.mitjul.dto.order.OrderStatusUpdateRequest;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private static final Long SEED_USER_ID = 1L;

    private final BookOrderRepository bookOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BookRepository bookRepository;
    private final QuoteCardRepository quoteCardRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    public OrderPreviewResponse preview(OrderRequest request) {
        validatePeriod(request.periodStart(), request.periodEnd());
        return buildPreview(request);
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        validatePeriod(request.periodStart(), request.periodEnd());
        User user = userRepository.findById(SEED_USER_ID)
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "기본 사용자를 찾을 수 없습니다."));

        OrderPreviewResponse preview = buildPreview(request);
        if (preview.books().isEmpty()) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "주문에 포함할 인용문이 없습니다.");
        }

        BookOrder order = bookOrderRepository.save(BookOrder.create(
            user,
            generateOrderNumber(),
            request.periodStart(),
            request.periodEnd(),
            request.coverStyle(),
            request.ownerName(),
            toSnapshotJson(preview)
        ));

        List<OrderItem> items = preview.books().stream()
            .map(book -> OrderItem.create(
                order,
                bookRepository.getReferenceById(book.bookId()),
                book.quoteCount().intValue(),
                preview.books().indexOf(book) + 1
            ))
            .toList();
        orderItemRepository.saveAll(items);

        return OrderResponse.from(order, items);
    }

    public List<OrderResponse> getOrders() {
        return bookOrderRepository.findByUserIdOrderByCreatedAtDesc(SEED_USER_ID).stream()
            .map(order -> OrderResponse.from(order, orderItemRepository.findByOrderIdOrderByDisplayOrderAsc(order.getId())))
            .toList();
    }

    public OrderResponse getOrder(Long orderId) {
        BookOrder order = getOrderEntity(orderId);
        return OrderResponse.from(order, orderItemRepository.findByOrderIdOrderByDisplayOrderAsc(order.getId()));
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatusUpdateRequest request) {
        BookOrder order = getOrderEntity(orderId);
        order.updateStatus(request.status());
        return OrderResponse.from(order, orderItemRepository.findByOrderIdOrderByDisplayOrderAsc(order.getId()));
    }

    private OrderPreviewResponse buildPreview(OrderRequest request) {
        LocalDateTime startAt = request.periodStart().atStartOfDay();
        LocalDateTime endAt = request.periodEnd().atTime(LocalTime.MAX);
        List<QuoteCardRepository.BookQuoteCount> quoteCounts = quoteCardRepository.countQuotesByBookInPeriod(
            SEED_USER_ID,
            startAt,
            endAt
        );
        Map<Long, Long> quoteCountByBookId = quoteCounts.stream()
            .collect(Collectors.toMap(
                QuoteCardRepository.BookQuoteCount::getBookId,
                QuoteCardRepository.BookQuoteCount::getQuoteCount,
                (left, right) -> left,
                LinkedHashMap::new
            ));
        Map<Long, Book> bookById = bookRepository.findByUserIdAndIdIn(SEED_USER_ID, quoteCountByBookId.keySet().stream().toList())
            .stream()
            .collect(Collectors.toMap(Book::getId, Function.identity()));
        List<OrderBookPreviewResponse> books = quoteCountByBookId.entrySet().stream()
            .map(entry -> OrderBookPreviewResponse.of(bookById.get(entry.getKey()), entry.getValue()))
            .sorted(Comparator.comparing(OrderBookPreviewResponse::bookId))
            .toList();
        long quoteCount = books.stream()
            .mapToLong(OrderBookPreviewResponse::quoteCount)
            .sum();

        return new OrderPreviewResponse(
            request.periodStart(),
            request.periodEnd(),
            request.coverStyle(),
            request.ownerName(),
            books.size(),
            quoteCount,
            books
        );
    }

    private String generateOrderNumber() {
        LocalDate today = LocalDate.now(clock);
        LocalDateTime startAt = today.atStartOfDay();
        LocalDateTime endAt = today.atTime(LocalTime.MAX);
        long sequence = bookOrderRepository.countByCreatedAtBetween(startAt, endAt) + 1;
        return "MJ-" + today.toString().replace("-", "") + "-" + String.format("%04d", sequence);
    }

    private String toSnapshotJson(OrderPreviewResponse preview) {
        try {
            return objectMapper.writeValueAsString(preview);
        } catch (JsonProcessingException exception) {
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "주문 스냅샷 생성에 실패했습니다.");
        }
    }

    private BookOrder getOrderEntity(Long orderId) {
        return bookOrderRepository.findById(orderId)
            .filter(order -> order.getUser().getId().equals(SEED_USER_ID))
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "주문을 찾을 수 없습니다."));
    }

    private void validatePeriod(LocalDate periodStart, LocalDate periodEnd) {
        if (periodEnd.isBefore(periodStart)) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "종료일은 시작일보다 빠를 수 없습니다.");
        }
    }
}
