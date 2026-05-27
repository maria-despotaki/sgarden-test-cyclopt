package com.sgarden.service;

import com.sgarden.dto.OrderItemRequest;
import com.sgarden.dto.OrderRequest;
import com.sgarden.model.Order;
import com.sgarden.model.OrderItem;
import com.sgarden.model.Product;
import com.sgarden.repository.OrderRepository;
import com.sgarden.repository.ProductRepository;
import static com.sgarden.util.ErrorMessages.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public List<Order> getAllOrders() {
        System.out.println("Fetching all orders");
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(String id) {
        System.out.println("Fetching order: " + id);
        return orderRepository.findById(id);
    }

    public Order createOrder(OrderRequest request) {
        List<OrderItem> items = new ArrayList<>();
        List<Product> resolvedProducts = new ArrayList<>();
        double total = 0.0;

        for (OrderItemRequest itemRequest : request.getItems()) {
            Optional<Product> productOpt = productRepository.findById(itemRequest.getProductId());
            if (productOpt.isEmpty()) {
                throw new RuntimeException(PRODUCT_NOT_FOUND + ": " + itemRequest.getProductId());
            }
            Product product = productOpt.get();
            int available = product.getStock() != null ? product.getStock() : 0;
            if (available < itemRequest.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + itemRequest.getProductId());
            }
            items.add(new OrderItem(product.getId(), itemRequest.getQuantity()));
            total += product.getPrice() * itemRequest.getQuantity();
            resolvedProducts.add(product);
        }

        for (int i = 0; i < resolvedProducts.size(); i++) {
            Product product = resolvedProducts.get(i);
            product.setStock(product.getStock() - request.getItems().get(i).getQuantity());
            productRepository.save(product);
        }

        Order order = new Order();
        order.setItems(items);
        order.setTotal(Math.round(total * 100.0) / 100.0);
        System.out.println("Creating order with " + items.size() + " item(s)");
        return orderRepository.save(order);
    }

    public Optional<Order> updateOrder(String id, OrderRequest request) {
        return orderRepository.findById(id).map(order -> {
            List<OrderItem> items = new ArrayList<>();
            double total = 0.0;

            for (OrderItemRequest itemRequest : request.getItems()) {
                Optional<Product> productOpt = productRepository.findById(itemRequest.getProductId());
                if (productOpt.isEmpty()) {
                    throw new RuntimeException(PRODUCT_NOT_FOUND + ": " + itemRequest.getProductId());
                }
                Product product = productOpt.get();
                items.add(new OrderItem(product.getId(), itemRequest.getQuantity()));
                total += product.getPrice() * itemRequest.getQuantity();
            }

            order.setItems(items);
            order.setTotal(Math.round(total * 100.0) / 100.0);
            System.out.println("Updating order: " + id);
            return orderRepository.save(order);
        });
    }

    public boolean deleteOrder(String id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            System.out.println("Deleted order: " + id);
            return true;
        }
        return false;
    }
}
