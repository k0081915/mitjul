package com.mitjul.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.mitjul.domain.book.Book;
import com.mitjul.domain.book.BookRepository;
import com.mitjul.domain.book.BookStatus;
import com.mitjul.domain.order.CoverStyle;
import com.mitjul.domain.order.OrderStatus;
import com.mitjul.domain.quote.QuoteCard;
import com.mitjul.domain.quote.QuoteCardRepository;
import com.mitjul.domain.user.User;
import com.mitjul.domain.user.UserRepository;
import com.mitjul.dto.order.OrderRequest;
import com.mitjul.dto.order.OrderStatusUpdateRequest;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Sql(statements = {
    "SET REFERENTIAL_INTEGRITY FALSE",
    "TRUNCATE TABLE quote_card_tags RESTART IDENTITY",
    "TRUNCATE TABLE order_items RESTART IDENTITY",
    "TRUNCATE TABLE orders RESTART IDENTITY",
    "TRUNCATE TABLE reviews RESTART IDENTITY",
    "TRUNCATE TABLE quote_cards RESTART IDENTITY",
    "TRUNCATE TABLE tags RESTART IDENTITY",
    "TRUNCATE TABLE books RESTART IDENTITY",
    "TRUNCATE TABLE users RESTART IDENTITY",
    "SET REFERENTIAL_INTEGRITY TRUE"
})
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private QuoteCardRepository quoteCardRepository;

    @Test
    void createOrderFromPreviewSnapshot() {
        User user = saveSeedUser();
        LocalDate today = LocalDate.now();
        Book firstBook = bookRepository.save(Book.create(
            user,
            "테스트 책 1",
            "테스트 저자 1",
            null,
            null,
            BookStatus.READING,
            today.minusDays(10),
            null
        ));
        Book secondBook = bookRepository.save(Book.create(
            user,
            "테스트 책 2",
            "테스트 저자 2",
            null,
            null,
            BookStatus.COMPLETED,
            today.minusDays(20),
            today
        ));

        quoteCardRepository.saveAll(List.of(
            QuoteCard.create(firstBook, 10, "첫 번째 인용문", null, List.of()),
            QuoteCard.create(firstBook, 20, "두 번째 인용문", null, List.of()),
            QuoteCard.create(secondBook, 30, "세 번째 인용문", null, List.of())
        ));
        quoteCardRepository.flush();

        OrderRequest request = new OrderRequest(today, today, CoverStyle.MINIMAL, "테스트 사용자");

        var preview = orderService.preview(request);

        assertThat(preview.bookCount()).isEqualTo(2);
        assertThat(preview.quoteCount()).isEqualTo(3);
        assertThat(preview.books().get(0).quotes())
            .extracting("content")
            .containsExactly("첫 번째 인용문", "두 번째 인용문");

        var order = orderService.createOrder(request);

        assertThat(order.orderNumber()).startsWith("MJ-");
        assertThat(order.status()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.items()).hasSize(2);
        assertThat(order.items().get(0).displayOrder()).isEqualTo(1);
        assertThat(order.items().get(0).quoteCount()).isEqualTo(2);
        assertThat(order.snapshotJson()).contains("\"ownerName\":\"테스트 사용자\"");
        assertThat(order.snapshotJson()).contains("\"content\":\"첫 번째 인용문\"");
        assertThat(orderService.getOrders())
            .singleElement()
            .satisfies(summary -> {
                assertThat(summary.id()).isEqualTo(order.id());
                assertThat(summary.bookCount()).isEqualTo(2);
                assertThat(summary.quoteCount()).isEqualTo(3);
            });
        assertThat(orderService.getOrder(order.id()).items()).hasSize(2);

        var updatedOrder = orderService.updateStatus(
            order.id(),
            new OrderStatusUpdateRequest(OrderStatus.PROCESSING)
        );

        assertThat(updatedOrder.status()).isEqualTo(OrderStatus.PROCESSING);
    }

    private User saveSeedUser() {
        User savedUser = userRepository.saveAndFlush(User.create("테스트 사용자", "order-test@example.com"));
        assertThat(savedUser.getId()).isEqualTo(1L);
        return savedUser;
    }
}
