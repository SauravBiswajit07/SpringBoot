package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

//    private List<Category> categories=new ArrayList<>();
//    private long nextId=1L; initial code before using db id generation

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper ;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
//      sorting order for pagination
        Sort sortByandOrder= sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
//      interface provided by spring data jpa for pagination
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sortByandOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        List<Category> categories = categoryPage.getContent();//getContent is inbuilt method for pageable/page interface
//      pagination code ends

        if (categories.isEmpty()) {//or simply categories.isEmpty()
            throw new APIException("No Categories Created till now");
        }
//        below is model mapper syntax used to convert/map each category type object
//        to CategoryDTO type object ...each has to be mapped hence the
//        list was first converted to stream the back to list after conversion bcz
//        Category Response Content is type of List<CategoryDTO>

        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category->modelMapper.map(category,CategoryDTO.class))
                .toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);

//      for pagination we are setting required data
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setPageNumber(categoryPage.getNumber());//or simply pageNumber from arguments
        categoryResponse.setPageSize(categoryPage.getSize());//or simply pageSize from arguments
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
        //        return categoryRepository.findAll(); before using category dto
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO,Category.class);
        Category existingCategory=categoryRepository.findByCategoryName(category.getCategoryName());
        if (!Objects.isNull(existingCategory)) {//or simply (existingCategory != NULL)
            throw new APIException("Category already exists with category name "+category.getCategoryName());
        }

//        category.setCategoryId(nextId++);
        Category savedCategory=categoryRepository.save(category);
        CategoryDTO savedCategoryDTO= modelMapper.map(savedCategory,CategoryDTO.class);
        return savedCategoryDTO;

    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->new ResourceNotFoundException("Category","CategoryID",categoryId));
        CategoryDTO deletedCategoryDTO= modelMapper.map(category,CategoryDTO.class);

        categoryRepository.delete(category);
        return deletedCategoryDTO;
//        return "category deleted successfully with id: "+categoryId;


//        List<Category> categories=categoryRepository.findAll();
//        Category category= categories.stream()
//                .filter(c-> Objects.equals(c.getCategoryId(), categoryId))
//                .findFirst()
//                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource Not Found"));
                //orElse(null);
               // .orElseThrow(() -> new RuntimeException("Category not found in Catgory Service Impl File"));
//        if(category==null){
//            return "Category Not Found";
//        }

    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Category category = modelMapper.map(categoryDTO,Category.class);
        Category savedCategory=categoryRepository.findById(categoryId).
                orElseThrow(()->new ResourceNotFoundException("Category","CategoryID",categoryId));

        category.setCategoryId(categoryId);
        savedCategory=categoryRepository.save(category);
        CategoryDTO savedCategoryDTO= modelMapper.map(savedCategory,CategoryDTO.class);
        return savedCategoryDTO;



//        without DB--->
//        List<Category> categories=categoryRepository.findAll();

//        Optional<Category> savedCategoryOptional=categoryRepository.findById(categoryId);


//        Optional<Category> optionalCategory= categories.stream()
//                .filter(c-> Objects.equals(c.getCategoryId(), categoryId))
//                .findFirst();
//
//        optionalCategory.ifPresent(updatedCategory -> updatedCategory.setCategoryName(category.getCategoryName()));
//
//        if(optionalCategory.isPresent()){
//            Category existingCategory=optionalCategory.get();
//            existingCategory.setCategoryName(category.getCategoryName());
//            Category savedCategory=categoryRepository.save(existingCategory);
//            return savedCategory;
//        }
//        else {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Category Not Found");
//        }
    }
}
