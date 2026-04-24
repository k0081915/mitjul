package com.mitjul.service;

import com.mitjul.domain.book.BookRepository;
import com.mitjul.domain.book.BookStatus;
import com.mitjul.domain.quote.QuoteCardRepository;
import com.mitjul.domain.review.ReviewRepository;
import com.mitjul.dto.dashboard.DashboardSummaryResponse;
import com.mitjul.dto.dashboard.RecentReviewResponse;
import com.mitjul.dto.quote.QuoteCardResponse;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private static final Long SEED_USER_ID = 1L;

    private final BookRepository bookRepository;
    private final QuoteCardRepository quoteCardRepository;
    private final ReviewRepository reviewRepository;
    private final Clock clock;

    public DashboardSummaryResponse getSummary(Integer year, Integer month) {
        YearMonth targetMonth = resolveTargetMonth(year, month);
        LocalDate monthStart = targetMonth.atDay(1);
        LocalDate monthEnd = targetMonth.atEndOfMonth();
        LocalDateTime monthStartAt = monthStart.atStartOfDay();
        LocalDateTime monthEndAt = monthEnd.atTime(LocalTime.MAX);
        LocalDateTime yearStartAt = LocalDate.of(targetMonth.getYear(), 1, 1).atStartOfDay();
        LocalDateTime yearEndAt = LocalDate.of(targetMonth.getYear(), 12, 31).atTime(LocalTime.MAX);

        long activeBookCount = bookRepository.countActiveBooksInPeriod(
            SEED_USER_ID,
            monthStart,
            monthEnd
        );
        long completedBookCount = bookRepository.countByUserIdAndStatusAndFinishedAtBetween(
            SEED_USER_ID,
            BookStatus.COMPLETED,
            monthStart,
            monthEnd
        );
        long quoteCount = quoteCardRepository.countByBookUserIdAndCreatedAtBetween(
            SEED_USER_ID,
            monthStartAt,
            monthEndAt
        );
        long reviewCount = reviewRepository.countByBookUserIdAndCreatedAtBetween(
            SEED_USER_ID,
            monthStartAt,
            monthEndAt
        );

        return new DashboardSummaryResponse(
            targetMonth.getYear(),
            targetMonth.getMonthValue(),
            activeBookCount,
            completedBookCount,
            quoteCount,
            reviewCount,
            quoteCardRepository.findTop5ByBookUserIdOrderByCreatedAtDesc(SEED_USER_ID)
                .stream()
                .map(QuoteCardResponse::from)
                .toList(),
            reviewRepository.findTop5ByBookUserIdOrderByCreatedAtDesc(SEED_USER_ID)
                .stream()
                .map(RecentReviewResponse::from)
                .toList(),
            quoteCardRepository.findTop5ByBookUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
                    SEED_USER_ID,
                    yearStartAt,
                    yearEndAt
                )
                .stream()
                .map(QuoteCardResponse::from)
                .toList()
        );
    }

    private YearMonth resolveTargetMonth(Integer year, Integer month) {
        LocalDate today = LocalDate.now(clock);
        int resolvedYear = year == null ? today.getYear() : year;
        int resolvedMonth = month == null ? today.getMonthValue() : month;
        return YearMonth.of(resolvedYear, resolvedMonth);
    }
}
