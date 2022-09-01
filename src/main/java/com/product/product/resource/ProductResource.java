package com.product.product.resource;

import com.product.product.domain.Category;
import com.product.product.domain.HttpResponse;
import com.product.product.domain.Product;
import com.product.product.exception.ExceptionHandling;
import com.product.product.exception.domain.*;
import com.product.product.service.CategoryService;
import com.product.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.product.product.constant.FileConstant.*;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping(path = { "/", "/product"})
public class ProductResource extends ExceptionHandling {
    public static final String PRODUCT_DELETED_SUCCESSFULLY = "Product deleted successfully";
    private ProductService productService;
    private CategoryService categoryService;

    public ProductResource(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @PostMapping("/add")
    public ResponseEntity<Product> addNewProduct(@RequestParam("name") String name,
                                                 @RequestParam("price") String price,
                                                 @RequestParam("currency") String currency,
                                                 @RequestParam("categoryName") String categoryName,
                                                 @RequestParam(value = "productImage", required = false) MultipartFile productImage) throws IOException, ProductNameExistException, ProductNameNotFoundException, NotAnImageFileException {
        Category category = categoryService.findCategoryByName(categoryName);
        Product newProduct = productService.addNewProduct(name, price, currency, category, productImage);
        return new ResponseEntity<>(newProduct, OK);
    }

    @PostMapping("/update")
    public ResponseEntity<Product> update(@RequestParam("currentName") String currentName,
                                          @RequestParam("name") String name,
                                          @RequestParam("price") String price,
                                          @RequestParam("currency") String currency,
                                          @RequestParam("categoryName") String categoryName,
                                          @RequestParam(value = "productImage", required = false) MultipartFile productImage) throws ProductNameExistException, ProductNameNotFoundException, IOException, NotAnImageFileException {
        Category category = categoryService.findCategoryByName(categoryName);
        Product updatedProduct = productService.updateProduct(currentName, name, price, currency, category, productImage);
        return new ResponseEntity<>(updatedProduct, OK);
    }

    @GetMapping("/find/{name}")
    public ResponseEntity<Product> getProduct(@PathVariable("name") String name) {
        Product product = productService.findProductByName(name);
        return new ResponseEntity<>(product, OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getProducts();
        return new ResponseEntity<>(products, OK);
    }

    @DeleteMapping("/delete/{name}")
    public ResponseEntity<HttpResponse> deleteProduct(@PathVariable("name") String name) throws IOException {
        productService.deleteProduct(name);
        return response(OK, PRODUCT_DELETED_SUCCESSFULLY);
    }

    @GetMapping(path = "/image/{name}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProductImage(@PathVariable("name") String name, @PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(PRODUCT_FOLDER + name + FORWARD_SLASH + fileName));
    }

    @PostMapping("/updateProductImage")
    public ResponseEntity<Product> updateProductImage(@RequestParam("name") String name, @RequestParam(value = "productImage") MultipartFile productImage) throws IOException, NotAnImageFileException, ProductNameNotFoundException, ProductNameExistException {
        Product product = productService.updateProductImage(name, productImage);
        return new ResponseEntity<>(product, OK);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }
}
