package com.mitjul.domain.order;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookOrderRepository extends JpaRepository<BookOrder, Long> {

    List<BookOrder> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<BookOrder> findByIdAndUserId(Long id, Long userId);

    Optional<BookOrder> findByOrderNumber(String orderNumber);
}
