package com.mitjul.domain.order;

import com.mitjul.domain.book.Book;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private BookOrder order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private Integer quoteCount;

    @Column(nullable = false)
    private Integer displayOrder;

    private OrderItem(BookOrder order, Book book, Integer quoteCount, Integer displayOrder) {
        this.order = order;
        this.book = book;
        this.quoteCount = quoteCount;
        this.displayOrder = displayOrder;
    }

    public static OrderItem create(BookOrder order, Book book, Integer quoteCount, Integer displayOrder) {
        return new OrderItem(order, book, quoteCount, displayOrder);
    }
}
