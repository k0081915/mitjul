package com.mitjul.api;

import com.mitjul.dto.order.OrderExportResponse;
import com.mitjul.dto.order.OrderPreviewResponse;
import com.mitjul.dto.order.OrderRequest;
import com.mitjul.dto.order.OrderResponse;
import com.mitjul.dto.order.OrderStatusUpdateRequest;
import com.mitjul.dto.order.OrderSummaryResponse;
import com.mitjul.service.OrderExportService;
import com.mitjul.service.OrderService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    private final OrderExportService orderExportService;

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
    public List<OrderSummaryResponse> getOrders() {
        return orderService.getOrders();
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable Long id) {
        return orderService.getOrder(id);
    }

    @GetMapping("/{id}/export/json")
    public ResponseEntity<OrderExportResponse> exportJson(@PathVariable Long id) {
        OrderExportResponse response = orderExportService.exportJson(id);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(response.orderNumber() + ".json"))
            .body(response);
    }

    @PatchMapping("/{id}/status")
    public OrderResponse updateStatus(
        @PathVariable Long id,
        @Valid @RequestBody OrderStatusUpdateRequest request
    ) {
        return orderService.updateStatus(id, request);
    }

    private String contentDisposition(String filename) {
        return ContentDisposition.attachment()
            .filename("mitjul-order-" + filename)
            .build()
            .toString();
    }
}
