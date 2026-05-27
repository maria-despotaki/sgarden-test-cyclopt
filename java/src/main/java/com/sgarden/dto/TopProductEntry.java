package com.sgarden.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopProductEntry {
    private String productId;
    private String name;
    private int totalQuantity;
    private double totalRevenue;
}
