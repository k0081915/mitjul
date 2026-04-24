package com.mitjul.domain.quote;

import com.mitjul.domain.book.Book;
import com.mitjul.domain.tag.Tag;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.EntityListeners;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "quote_cards")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuoteCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private Integer page;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @ManyToMany
    @JoinTable(
        name = "quote_card_tags",
        joinColumns = @JoinColumn(name = "quote_card_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new LinkedHashSet<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private QuoteCard(Book book, Integer page, String content, String memo, Collection<Tag> tags) {
        this.book = book;
        this.page = page;
        this.content = content;
        this.memo = memo;
        replaceTags(tags);
    }

    public static QuoteCard create(
        Book book,
        Integer page,
        String content,
        String memo,
        Collection<Tag> tags
    ) {
        return new QuoteCard(book, page, content, memo, tags);
    }

    public void update(Integer page, String content, String memo, Collection<Tag> tags) {
        if (page != null) {
            this.page = page;
        }
        if (content != null) {
            this.content = content;
        }
        if (memo != null) {
            this.memo = memo;
        }
        if (tags != null) {
            replaceTags(tags);
        }
    }

    private void replaceTags(Collection<Tag> tags) {
        this.tags.clear();
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

}
