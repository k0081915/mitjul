package com.mitjul.domain.book;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Book> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, BookStatus status);

    List<Book> findByUserIdAndIdIn(Long userId, List<Long> ids);

    long countByUserIdAndStatusAndFinishedAtBetween(
        Long userId,
        BookStatus status,
        LocalDate startDate,
        LocalDate endDate
    );

    @Query("""
        select count(b)
        from Book b
        where b.user.id = :userId
          and b.startedAt <= :endDate
          and (b.finishedAt is null or b.finishedAt >= :startDate)
        """)
    long countActiveBooksInPeriod(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
