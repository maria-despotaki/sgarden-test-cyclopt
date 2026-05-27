package com.sgarden.dto;

import com.sgarden.validation.OnCreate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {

    @NotEmpty(groups = OnCreate.class, message = "Items are required")
    private List<@Valid OrderItemRequest> items;
}
