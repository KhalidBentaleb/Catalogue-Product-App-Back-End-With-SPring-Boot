package com.product.product.service.impl;

import com.product.product.domain.Category;
import com.product.product.domain.Product;
import com.product.product.exception.domain.*;
import com.product.product.repository.ProductRepository;
import com.product.product.service.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static com.product.product.constant.FileConstant.*;
import static com.product.product.constant.FileConstant.JPG_EXTENSION;
import static com.product.product.constant.ProductImplConstant.NO_PRODUCT_FOUND_BY_NAME;
import static com.product.product.constant.ProductImplConstant.PRODUCT_NAME_ALREADY_EXISTS;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.MediaType.*;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product addNewProduct(String name, String price, String currency, Category category, MultipartFile productImage) throws ProductNameExistException, ProductNameNotFoundException, IOException, NotAnImageFileException {
        validateNewProductName(EMPTY, name);
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setCurrency(currency);
        product.setCategory(category);
        product.setProductImageUrl(getTemporaryProductImageUrl(name));
        productRepository.save(product);
        saveProductImage(product, productImage);
        return product;
    }

    @Override
    public Product updateProduct(String currentName, String newName, String newPrice, String newCurrency, Category category, MultipartFile productImage) throws ProductNameExistException, ProductNameNotFoundException, IOException, NotAnImageFileException {
        Product currentProduct = validateNewProductName(currentName, newName);
        currentProduct.setName(newName);
        currentProduct.setPrice(newPrice);
        currentProduct.setCurrency(newCurrency);
        currentProduct.setCategory(category);
        productRepository.save(currentProduct);
        saveProductImage(currentProduct, productImage);
        return currentProduct;
    }

    @Override
    public Product updateProductImage(String name, MultipartFile productImage) throws ProductNameExistException, ProductNameNotFoundException, IOException, NotAnImageFileException {
        Product product = validateNewProductName(name, null);
        saveProductImage(product, productImage);
        return product;
    }

    @Override
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product findProductByName(String name) {
        return productRepository.findProductByName(name);
    }

    @Override
    public void deleteProduct(String name) throws IOException {
        Product product = productRepository.findProductByName(name);
        Path productFolder = Paths.get(PRODUCT_FOLDER + product.getName()).toAbsolutePath().normalize();
        FileUtils.deleteDirectory(new File(productFolder.toString()));
        productRepository.deleteById(product.getId());
    }

    private void saveProductImage(Product product, MultipartFile productImage) throws IOException, NotAnImageFileException {
        if (productImage != null) {
            if(!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(productImage.getContentType())) {
                throw new NotAnImageFileException(productImage.getOriginalFilename() + NOT_AN_IMAGE_FILE);
            }
            Path userFolder = Paths.get(PRODUCT_FOLDER + product.getName()).toAbsolutePath().normalize();
            if(!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                LOGGER.info(DIRECTORY_CREATED + userFolder);
            }
            Files.deleteIfExists(Paths.get(userFolder + product.getName() + DOT + JPG_EXTENSION));
            Files.copy(productImage.getInputStream(), userFolder.resolve(product.getName() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
            product.setProductImageUrl(setProductImageUrl(product.getName()));
            productRepository.save(product);
            LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + productImage.getOriginalFilename());
        }
    }

    private String setProductImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(PRODUCT_IMAGE_PATH + username + FORWARD_SLASH
                + username + DOT + JPG_EXTENSION).toUriString();
    }

    private String getTemporaryProductImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_PRODUCT_IMAGE_PATH + username).toUriString();
    }

    private Product validateNewProductName(String currentName, String newName) throws ProductNameExistException, ProductNameNotFoundException {
        Product productByNewName = findProductByName(newName);
        if(StringUtils.isNotBlank(currentName)) {
            Product currentProduct = findProductByName(currentName);
            if(currentProduct == null) {
                throw new ProductNameNotFoundException(NO_PRODUCT_FOUND_BY_NAME + currentName);
            }
            if(productByNewName != null && !currentProduct.getId().equals(productByNewName.getId())) {
                throw new ProductNameExistException(PRODUCT_NAME_ALREADY_EXISTS);
            }
            return currentProduct;
        } else {
            if(productByNewName != null) {
                throw new ProductNameExistException(PRODUCT_NAME_ALREADY_EXISTS);
            }
            return null;
        }
    }

}
