package com.mitjul.domain.book;

import com.mitjul.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "books")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 100)
    private String author;

    @Column(length = 20)
    private String isbn;

    @Column(length = 500)
    private String coverImageUrl;

    @Column(length = 50)
    private String presetCoverKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookStatus status;

    @Column(nullable = false)
    private LocalDate startedAt;

    private LocalDate finishedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private Book(
        User user,
        String title,
        String author,
        String isbn,
        String coverImageUrl,
        String presetCoverKey,
        BookStatus status,
        LocalDate startedAt,
        LocalDate finishedAt
    ) {
        this.user = user;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.coverImageUrl = coverImageUrl;
        this.presetCoverKey = presetCoverKey;
        this.status = status;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
    }

    public static Book create(
        User user,
        String title,
        String author,
        String isbn,
        String coverImageUrl,
        String presetCoverKey,
        BookStatus status,
        LocalDate startedAt,
        LocalDate finishedAt
    ) {
        return new Book(
            user,
            title,
            author,
            isbn,
            coverImageUrl,
            presetCoverKey,
            status == null ? BookStatus.READING : status,
            startedAt,
            finishedAt
        );
    }

    public void update(
        String title,
        String author,
        String isbn,
        String coverImageUrl,
        String presetCoverKey,
        BookStatus status,
        LocalDate startedAt,
        LocalDate finishedAt
    ) {
        if (title != null) {
            this.title = title;
        }
        if (author != null) {
            this.author = author;
        }
        if (isbn != null) {
            this.isbn = isbn;
        }
        if (coverImageUrl != null) {
            this.coverImageUrl = coverImageUrl;
        }
        if (presetCoverKey != null) {
            this.presetCoverKey = presetCoverKey;
        }
        if (status != null) {
            this.status = status;
        }
        if (startedAt != null) {
            this.startedAt = startedAt;
        }
        if (finishedAt != null) {
            this.finishedAt = finishedAt;
        }
    }

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
