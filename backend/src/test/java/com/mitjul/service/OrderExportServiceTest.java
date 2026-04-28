package com.mitjul.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mitjul.common.error.ApiException;
import com.mitjul.common.error.ErrorCode;
import com.mitjul.domain.book.Book;
import com.mitjul.domain.book.BookRepository;
import com.mitjul.domain.book.BookStatus;
import com.mitjul.domain.order.CoverStyle;
import com.mitjul.domain.quote.QuoteCard;
import com.mitjul.domain.quote.QuoteCardRepository;
import com.mitjul.domain.user.User;
import com.mitjul.domain.user.UserRepository;
import com.mitjul.dto.order.OrderRequest;
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
class OrderExportServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderExportService orderExportService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private QuoteCardRepository quoteCardRepository;

    @Test
    void exportOrderAsJsonFromSnapshot() {
        User user = saveSeedUser();
        LocalDate today = LocalDate.now();
        Book book = bookRepository.save(Book.create(
            user,
            "스냅샷 테스트 책",
            "테스트 저자",
            null,
            null,
            BookStatus.READING,
            today.minusDays(3),
            null
        ));
        quoteCardRepository.save(QuoteCard.create(book, 12, "스냅샷에 남길 문장", "주문 당시 메모", List.of()));
        quoteCardRepository.flush();

        var order = orderService.createOrder(new OrderRequest(today, today, CoverStyle.MINIMAL, "내보내기 사용자"));
        book.update("수정된 책 제목", null, null, null, null, null, null);
        bookRepository.flush();

        var export = orderExportService.exportJson(order.id());

        assertThat(export.orderNumber()).isEqualTo(order.orderNumber());
        assertThat(export.status()).isEqualTo(order.status());
        assertThat(export.createdAt()).isEqualTo(order.createdAt());
        assertThat(export.exportedAt()).isNotNull();
        assertThat(export.snapshot().get("ownerName").asText()).isEqualTo("내보내기 사용자");
        assertThat(export.snapshot().get("bookCount").asLong()).isEqualTo(1);
        assertThat(export.snapshot().get("quoteCount").asLong()).isEqualTo(1);
        assertThat(export.snapshot().get("books").get(0).get("title").asText()).isEqualTo("스냅샷 테스트 책");
        assertThat(export.snapshot().get("books").get(0).get("quotes").get(0).get("content").asText())
            .isEqualTo("스냅샷에 남길 문장");
        assertThat(export.snapshot().get("books").get(0).get("quotes").get(0).get("memo").asText())
            .isEqualTo("주문 당시 메모");
    }

    @Test
    void exportMissingOrderFails() {
        assertThatThrownBy(() -> orderExportService.exportJson(999L))
            .isInstanceOf(ApiException.class)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.NOT_FOUND);
    }

    private User saveSeedUser() {
        User savedUser = userRepository.saveAndFlush(User.create("테스트 사용자", "order-export-test@example.com"));
        assertThat(savedUser.getId()).isEqualTo(1L);
        return savedUser;
    }
}
