package com.ecommerce.project.repositories;

import com.ecommerce.project.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByCategoryName(@NotBlank(message = "Name Field Can't be Blank ") @Size(min = 3,message = "at least 3 characters needed for name field") String categoryName);
}
