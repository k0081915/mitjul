package com.mitjul.dto.dashboard;

import com.mitjul.dto.quote.QuoteCardResponse;
import java.util.List;

public record DashboardSummaryResponse(
    int year,
    int month,
    long activeBookCount,
    long completedBookCount,
    long quoteCount,
    long reviewCount,
    List<QuoteCardResponse> recentQuotes,
    List<RecentReviewResponse> recentReviews,
    List<QuoteCardResponse> yearlyQuotes
) {
}
