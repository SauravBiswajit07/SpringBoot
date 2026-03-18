package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FileService fileService;

    @Value("{project.image}")
    private String path;


    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        Product product=modelMapper.map(productDTO,Product.class);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","categoryId",categoryId));

//        validation
        boolean productExist=false;
        List<Product>products=category.getProductList();
        for(Product p:products){
            if(p.getProductName().equals(product.getProductName())){
                productExist=true;
                break;
            }
        }
        if(productExist){
            throw new APIException("Product already exists !!");
        }
        else {
            product.setCategory(category);
            product.setImage("default.png");

            Double specialprice = (product.getPrice()) - ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialprice);
            Product savedProduct = productRepository.save(product);

            return modelMapper.map(savedProduct, ProductDTO.class);
        }
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
//        Sorting and Pagination
        Sort sortByandOrder= sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
//      interface provided by spring data jpa for pagination
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sortByandOrder);
        Page<Product> productPage = productRepository.findAll(pageable);

//        System.out.println("For debugging");
//        System.out.println(productPage.getContent());
//        System.out.println(typeOf(productPage.getTotalPages()));

        List<Product> products = productPage.getContent();
        if (products.isEmpty()) {
            throw new APIException("No Products Found");
        }
        List<ProductDTO>productDTOS= products.stream()
                .map(product->modelMapper.map(product,ProductDTO.class)).toList();
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages((long) productPage.getTotalPages());//in built return is Int
        productResponse.setLastPage(productPage.isLast());


        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","categoryId",categoryId));
//        List<Product> products = productRepository.findByCategory(category);
        Sort sortByandOrder= sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
//      interface provided by spring data jpa for pagination
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sortByandOrder);
        Page<Product> productPage = productRepository.findByCategoryOrderByPriceAsc(category,pageable);

        List<Product> products = productPage.getContent();
                //productRepository.findByCategoryOrderByPriceAsc(category);
        if(products.isEmpty()) {
            throw new APIException("No Products Found");
        }
        List<ProductDTO>productDTOS= products.stream()
                .map(product->modelMapper.map(product,ProductDTO.class)).toList();
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages((long) productPage.getTotalPages());//in built return is Int
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByandOrder= sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
//      interface provided by spring data jpa for pagination
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sortByandOrder);
        Page<Product> productPage = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%',pageable);

        List<Product> products = productPage.getContent();

//        List<Product> products = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%', pageable);
//        List<Product> products = productRepository.findByProductNameContainsIgnoreCase(keyword); also valid and easy
        if(products.isEmpty()) {
            throw new APIException("No Products Found");
        }
        List<ProductDTO>productDTOS= products.stream()
                .map(product->modelMapper.map(product,ProductDTO.class)).toList();
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages((long) productPage.getTotalPages());//in built return is Int
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId,ProductDTO productDTO) {

        Product product=modelMapper.map(productDTO,Product.class);
        Product productFromDb= productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","productId",productId));

        productFromDb.setPrice(product.getPrice());
        productFromDb.setProductName(product.getProductName());
        productFromDb.setDescription(product.getDescription());
        productFromDb.setQuantity(product.getQuantity());
        productFromDb.setDiscount(product.getDiscount());
//        Double specialprice= (product.getPrice())-((product.getDiscount() * 0.01)* product.getPrice());
        productFromDb.setSpecialPrice(product.getSpecialPrice());

        Product updatedProduct=productRepository.save(productFromDb);

        return modelMapper.map(updatedProduct,ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product productFromDb= productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","productId",productId));
        productRepository.delete(productFromDb);

        return modelMapper.map(productFromDb,ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        //Get product from db
        Product productFromDb= productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","productId",productId));

        //upload image to server
        //get the file name pf uploaded image
//        String path="images/"; directly set in application.properties
        String fileName= fileService.uploadImage(path,image);

        //updating the new file name to product
        productFromDb.setImage(fileName);

        //save updated product
        Product updatedProduct=productRepository.save(productFromDb);

        //return dto after mapping product to dto
        return modelMapper.map(updatedProduct,ProductDTO.class);
    }



}
