package com.product.product.service;

import com.product.product.domain.Category;
import com.product.product.domain.Product;
import com.product.product.exception.domain.NotAnImageFileException;
import com.product.product.exception.domain.ProductNameExistException;
import com.product.product.exception.domain.ProductNameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    List<Product> getProducts();

    Product findProductByName(String name);

    Product addNewProduct(String name, String price, String currency, Category category, MultipartFile productImage) throws ProductNameExistException, ProductNameNotFoundException, IOException, NotAnImageFileException;

    Product updateProduct(String currentName, String newName, String newPrice, String newCurrency, Category category, MultipartFile productImage) throws ProductNameExistException, ProductNameNotFoundException, IOException, NotAnImageFileException;

    void deleteProduct(String name) throws IOException;

    Product updateProductImage(String name, MultipartFile productImage) throws ProductNameExistException, ProductNameNotFoundException, IOException, NotAnImageFileException;
}
