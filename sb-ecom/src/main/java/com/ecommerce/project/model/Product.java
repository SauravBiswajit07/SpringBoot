package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;
    @NotBlank
    @Size(min=3,message = "Product name should be atleast 3 chracters")
    private String productName;
    @NotBlank
    @Size(min=6,message = "Product Description should be atleast 6 chracters")
    private String description;
    private String image;
    private double price;
    private double discount;
    private Integer quantity;
    private Double specialPrice;

    @ManyToOne
    @JoinColumn//(name = "category_id")
    private Category category;
}
