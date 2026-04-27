package com.mitjul.domain.order;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderIdOrderByDisplayOrderAsc(Long orderId);

    @Query("""
        select oi
        from OrderItem oi
        where oi.order.id in :orderIds
        order by oi.order.id asc, oi.displayOrder asc
        """)
    List<OrderItem> findByOrderIdsOrderByDisplayOrderAsc(@Param("orderIds") List<Long> orderIds);
}
