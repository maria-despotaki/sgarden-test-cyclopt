package com.sgarden.dto;

import com.sgarden.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusRequest {

    @NotNull(message = "status is required")
    private OrderStatus status;
}
