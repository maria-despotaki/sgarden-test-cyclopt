package com.sgarden.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesAnalyticsResponse {
    private double totalRevenue;
    private int totalOrders;
    private List<TopProductEntry> topProducts;
    private Map<String, Double> revenueByPeriod;
}
