package com.sgarden.service;

import com.sgarden.dto.AlertResponse;
import com.sgarden.repository.ProductRepository;
import static com.sgarden.util.AlertConstants.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class AlertService {

    private final ProductRepository productRepository;
    private final AtomicInteger threshold = new AtomicInteger(DEFAULT_THRESHOLD);

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
        if (stock < t * CRITICAL_RATIO) return SEVERITY_CRITICAL;
        if (stock < t * WARNING_RATIO) return SEVERITY_WARNING;
        return SEVERITY_INFO;
    }
}
