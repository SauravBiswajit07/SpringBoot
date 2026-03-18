package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {//this was created as virtual model like to
    // decouple betwn model and controller and we can add other parameters without
// changing real model structure, and we are passing this as list to category response
    private Long categoryId;
    private String categoryName;

}
