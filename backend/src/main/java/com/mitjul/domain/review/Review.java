package com.mitjul.domain.review;

import com.mitjul.domain.book.Book;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.EntityListeners;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "reviews")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false, unique = true)
    private Book book;

    @Column(nullable = false)
    private Byte rating;

    @Column(nullable = false, length = 200)
    private String oneLiner;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private Review(Book book, Byte rating, String oneLiner, String body) {
        this.book = book;
        this.rating = rating;
        this.oneLiner = oneLiner;
        this.body = body;
    }

    public static Review create(Book book, Byte rating, String oneLiner, String body) {
        return new Review(book, rating, oneLiner, body);
    }

    public void update(Byte rating, String oneLiner, String body) {
        this.rating = rating;
        this.oneLiner = oneLiner;
        this.body = body;
    }

}
