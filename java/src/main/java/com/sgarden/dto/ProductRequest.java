package com.sgarden.dto;

import com.sgarden.validation.OnCreate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductRequest {

    @NotBlank(groups = OnCreate.class, message = "Name is required")
    private String name;

    private String description;

    @Pattern(
        regexp = "^(Electronics|Accessories|Storage|Networking)$",
        message = "Category must be one of: Electronics, Accessories, Storage, Networking"
    )
    private String category;

    @NotNull(groups = OnCreate.class, message = "Price is required")
    @Positive(groups = OnCreate.class, message = "Price must be a positive number")
    private Double price;

    private Integer stock;
}
