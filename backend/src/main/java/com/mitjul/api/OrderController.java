package com.mitjul.api;

import com.mitjul.dto.order.OrderPreviewResponse;
import com.mitjul.dto.order.OrderRequest;
import com.mitjul.dto.order.OrderResponse;
import com.mitjul.dto.order.OrderStatusUpdateRequest;
import com.mitjul.service.OrderService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/preview")
    public OrderPreviewResponse preview(@Valid @RequestBody OrderRequest request) {
        return orderService.preview(request);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity
            .created(URI.create("/api/orders/" + response.id()))
            .body(response);
    }

    @GetMapping
    public List<OrderResponse> getOrders() {
        return orderService.getOrders();
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable Long id) {
        return orderService.getOrder(id);
    }

    @PatchMapping("/{id}/status")
    public OrderResponse updateStatus(
        @PathVariable Long id,
        @Valid @RequestBody OrderStatusUpdateRequest request
    ) {
        return orderService.updateStatus(id, request);
    }
}
