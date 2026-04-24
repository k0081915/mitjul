package com.mitjul.domain.book;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Book> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, BookStatus status);
}
