package com.mitjul.domain.order;

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
import jakarta.persistence.Table;
import jakarta.persistence.EntityListeners;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 20)
    private String orderNumber;

    @Column(nullable = false)
    private LocalDate periodStart;

    @Column(nullable = false)
    private LocalDate periodEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CoverStyle coverStyle;

    @Column(nullable = false, length = 50)
    private String ownerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(nullable = false, columnDefinition = "json")
    private String snapshotJson;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private BookOrder(
        User user,
        String orderNumber,
        LocalDate periodStart,
        LocalDate periodEnd,
        CoverStyle coverStyle,
        String ownerName,
        String snapshotJson
    ) {
        this.user = user;
        this.orderNumber = orderNumber;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.coverStyle = coverStyle;
        this.ownerName = ownerName;
        this.status = OrderStatus.PENDING;
        this.snapshotJson = snapshotJson;
    }

    public static BookOrder create(
        User user,
        String orderNumber,
        LocalDate periodStart,
        LocalDate periodEnd,
        CoverStyle coverStyle,
        String ownerName,
        String snapshotJson
    ) {
        return new BookOrder(user, orderNumber, periodStart, periodEnd, coverStyle, ownerName, snapshotJson);
    }

    public void updateStatus(OrderStatus status) {
        if (status != null) {
            this.status = status;
        }
    }
}
