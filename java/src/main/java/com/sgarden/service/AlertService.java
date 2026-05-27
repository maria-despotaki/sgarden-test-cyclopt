package com.sgarden.service;

import com.sgarden.dto.AlertResponse;
import com.sgarden.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class AlertService {

    private final ProductRepository productRepository;
    private final AtomicInteger threshold = new AtomicInteger(10);

    public AlertService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<AlertResponse> getAlerts() {
        int t = threshold.get();
        return productRepository.findAll().stream()
                .filter(p -> p.getStock() != null && p.getStock() < t)
                .map(p -> new AlertResponse(p.getName(), p.getStock(), severity(p.getStock(), t)))
                .collect(Collectors.toList());
    }

    public int setThreshold(int value) {
        threshold.set(value);
        return value;
    }

    public int getThreshold() {
        return threshold.get();
    }

    private String severity(int stock, int t) {
        if (stock < t * 0.25) return "critical";
        if (stock < t * 0.5) return "warning";
        return "info";
    }
}
