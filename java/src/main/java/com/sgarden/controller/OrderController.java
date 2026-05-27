package com.sgarden.controller;

import com.sgarden.dto.ErrorResponse;
import com.sgarden.dto.OrderRequest;
import com.sgarden.model.Order;
import com.sgarden.service.OrderService;
import static com.sgarden.util.ErrorMessages.*;
import com.sgarden.validation.OnCreate;
import jakarta.validation.groups.Default;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable String id) {
        return orderService.getOrderById(id)
                .map(order -> ResponseEntity.ok((Object) order))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(ORDER_NOT_FOUND)));
    }

    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestBody @Validated({OnCreate.class, Default.class}) OrderRequest request) {
        try {
            Order order = orderService.createOrder(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(
            @PathVariable String id,
            @RequestBody @Validated({OnCreate.class, Default.class}) OrderRequest request) {
        try {
            return orderService.updateOrder(id, request)
                    .map(order -> ResponseEntity.ok((Object) order))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ErrorResponse(ORDER_NOT_FOUND)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable String id) {
        if (orderService.deleteOrder(id)) {
            return ResponseEntity.ok(Map.of("message", "Order deleted"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ORDER_NOT_FOUND));
    }
}
