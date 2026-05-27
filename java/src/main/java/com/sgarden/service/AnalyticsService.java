package com.sgarden.service;

import com.sgarden.dto.SalesAnalyticsResponse;
import com.sgarden.dto.TopProductEntry;
import com.sgarden.model.Order;
import com.sgarden.model.OrderItem;
import com.sgarden.model.Product;
import com.sgarden.repository.OrderRepository;
import com.sgarden.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private static final DateTimeFormatter PERIOD_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM").withZone(ZoneOffset.UTC);

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public AnalyticsService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public SalesAnalyticsResponse getSalesAnalytics(Instant start, Instant end) {
        List<Order> orders = fetchOrders(start, end);

        double totalRevenue = orders.stream()
                .mapToDouble(o -> o.getTotal() != null ? o.getTotal() : 0.0)
                .sum();
        totalRevenue = Math.round(totalRevenue * 100.0) / 100.0;

        Set<String> productIds = orders.stream()
                .filter(o -> o.getItems() != null)
                .flatMap(o -> o.getItems().stream())
                .map(OrderItem::getProductId)
                .collect(Collectors.toSet());

        Map<String, Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        Map<String, double[]> aggregates = new LinkedHashMap<>();
        for (Order order : orders) {
            if (order.getItems() == null) continue;
            for (OrderItem item : order.getItems()) {
                String pid = item.getProductId();
                double[] agg = aggregates.computeIfAbsent(pid, k -> new double[]{0, 0});
                agg[0] += item.getQuantity();
                Product product = productMap.get(pid);
                if (product != null && product.getPrice() != null) {
                    agg[1] += item.getQuantity() * product.getPrice();
                }
            }
        }

        List<TopProductEntry> topProducts = aggregates.entrySet().stream()
                .map(e -> {
                    String pid = e.getKey();
                    Product p = productMap.get(pid);
                    double revenue = Math.round(e.getValue()[1] * 100.0) / 100.0;
                    return new TopProductEntry(pid, p != null ? p.getName() : "Unknown",
                            (int) e.getValue()[0], revenue);
                })
                .sorted(Comparator.comparingInt(TopProductEntry::getTotalQuantity).reversed())
                .collect(Collectors.toList());

        Map<String, Double> revenueByPeriod = new TreeMap<>();
        for (Order order : orders) {
            if (order.getCreatedAt() == null) continue;
            String period = PERIOD_FORMATTER.format(order.getCreatedAt());
            double orderTotal = order.getTotal() != null ? order.getTotal() : 0.0;
            revenueByPeriod.merge(period, orderTotal, Double::sum);
        }
        revenueByPeriod.replaceAll((k, v) -> Math.round(v * 100.0) / 100.0);

        return new SalesAnalyticsResponse(totalRevenue, orders.size(), topProducts, revenueByPeriod);
    }

    private List<Order> fetchOrders(Instant start, Instant end) {
        if (start != null && end != null) {
            return orderRepository.findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(start, end);
        } else if (start != null) {
            return orderRepository.findByCreatedAtGreaterThanEqual(start);
        } else if (end != null) {
            return orderRepository.findByCreatedAtLessThan(end);
        } else {
            return orderRepository.findAll();
        }
    }
}
