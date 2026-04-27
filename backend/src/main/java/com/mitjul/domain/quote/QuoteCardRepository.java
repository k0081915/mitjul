package com.mitjul.domain.quote;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuoteCardRepository extends JpaRepository<QuoteCard, Long> {

    interface BookQuoteCount {
        Long getBookId();

        Long getQuoteCount();
    }

    List<QuoteCard> findByBookIdOrderByCreatedAtDesc(Long bookId);

    long countByBookUserIdAndCreatedAtBetween(
        Long userId,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
    );

    List<QuoteCard> findTop5ByBookUserIdOrderByCreatedAtDesc(Long userId);

    List<QuoteCard> findTop5ByBookUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
        Long userId,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
    );

    @Query("""
        select distinct q
        from QuoteCard q
        join q.book b
        left join q.tags t
        where b.user.id = :userId
          and (:keyword is null or lower(q.content) like lower(concat('%', :keyword, '%'))
            or lower(q.memo) like lower(concat('%', :keyword, '%')))
          and (:bookId is null or b.id = :bookId)
          and (:tagName is null or t.name = :tagName)
        order by q.createdAt desc
        """)
    List<QuoteCard> search(
        @Param("userId") Long userId,
        @Param("keyword") String keyword,
        @Param("bookId") Long bookId,
        @Param("tagName") String tagName
    );

    @Query("""
        select q.book.id as bookId, count(q) as quoteCount
        from QuoteCard q
        where q.book.user.id = :userId
          and q.createdAt between :startDateTime and :endDateTime
        group by q.book.id
        order by min(q.createdAt) asc
        """)
    List<BookQuoteCount> countQuotesByBookInPeriod(
        @Param("userId") Long userId,
        @Param("startDateTime") LocalDateTime startDateTime,
        @Param("endDateTime") LocalDateTime endDateTime
    );

    @Query("""
        select distinct q
        from QuoteCard q
        join fetch q.book b
        left join fetch q.tags t
        where b.user.id = :userId
          and b.id in :bookIds
          and q.createdAt between :startDateTime and :endDateTime
        order by q.createdAt asc
        """)
    List<QuoteCard> findSnapshotQuotesByBookIdsInPeriod(
        @Param("userId") Long userId,
        @Param("bookIds") List<Long> bookIds,
        @Param("startDateTime") LocalDateTime startDateTime,
        @Param("endDateTime") LocalDateTime endDateTime
    );
}
