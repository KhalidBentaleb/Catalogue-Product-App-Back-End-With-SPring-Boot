package com.product.product.service;

import com.product.product.domain.Category;
import com.product.product.exception.domain.CategoryNameExistException;
import com.product.product.exception.domain.CategoryNameNotFoundException;
import com.product.product.exception.domain.NotAnImageFileException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CategoryService {

    List<Category> getCategories();

    Category findCategoryByName(String name);

    Category addNewCategory(String name, MultipartFile categoryImage) throws CategoryNameExistException, CategoryNameNotFoundException, IOException, NotAnImageFileException;

    Category updateCategory(String currentName, String newName, MultipartFile categoryImage) throws CategoryNameExistException, CategoryNameNotFoundException, IOException, NotAnImageFileException;

    void deleteCategory(String name) throws IOException;

    Category updateCategoryImage(String name, MultipartFile categoryImage) throws CategoryNameExistException, CategoryNameNotFoundException, IOException, NotAnImageFileException;
}
