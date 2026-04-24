package com.mitjul.domain.quote;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuoteCardRepository extends JpaRepository<QuoteCard, Long> {

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
        where (:keyword is null or lower(q.content) like lower(concat('%', :keyword, '%'))
            or lower(q.memo) like lower(concat('%', :keyword, '%')))
          and (:bookId is null or b.id = :bookId)
          and (:tagName is null or t.name = :tagName)
        order by q.createdAt desc
        """)
    List<QuoteCard> search(
        @Param("keyword") String keyword,
        @Param("bookId") Long bookId,
        @Param("tagName") String tagName
    );
}
