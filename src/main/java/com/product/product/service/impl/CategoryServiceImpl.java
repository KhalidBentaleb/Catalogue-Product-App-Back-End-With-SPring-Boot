package com.product.product.service.impl;

import com.product.product.domain.Category;
import com.product.product.exception.domain.CategoryNameExistException;
import com.product.product.exception.domain.CategoryNameNotFoundException;
import com.product.product.exception.domain.NotAnImageFileException;
import com.product.product.repository.CategoryRepository;
import com.product.product.service.CategoryService;
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

import static com.product.product.constant.CategoryImplConstant.CATEGORY_NAME_ALREADY_EXISTS;
import static com.product.product.constant.CategoryImplConstant.NO_CATEGORY_FOUND_BY_NAME;
import static com.product.product.constant.FileConstant.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.MediaType.*;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category addNewCategory(String name, MultipartFile categoryImage) throws CategoryNameNotFoundException, CategoryNameExistException, IOException, NotAnImageFileException {
        validateNewCategoryName(EMPTY, name);
        Category category = new Category();
        category.setName(name);
        category.setCategoryImageUrl(getTemporaryCategoryImageUrl(name));
        categoryRepository.save(category);
        saveCategoryImage(category, categoryImage);
        return category;
    }

    @Override
    public Category updateCategory(String currentName, String newName, MultipartFile categoryImage) throws CategoryNameExistException, CategoryNameNotFoundException, IOException, NotAnImageFileException {
        Category currentCategory = validateNewCategoryName(currentName, newName);
        currentCategory.setName(newName);
        categoryRepository.save(currentCategory);
        saveCategoryImage(currentCategory, categoryImage);
        return currentCategory;
    }

    @Override
    public Category updateCategoryImage(String name, MultipartFile categoryImage) throws CategoryNameExistException, CategoryNameNotFoundException, IOException, NotAnImageFileException {
        Category category = validateNewCategoryName(name, null);
        saveCategoryImage(category, categoryImage);
        return category;
    }

    @Override
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findCategoryByName(String name) {
        return categoryRepository.findCategoryByName(name);
    }

    @Override
    public void deleteCategory(String name) throws IOException {
        Category category = categoryRepository.findCategoryByName(name);
        Path categoryFolder = Paths.get(CATEGORY_FOLDER + category.getName()).toAbsolutePath().normalize();
        FileUtils.deleteDirectory(new File(categoryFolder.toString()));
        categoryRepository.deleteById(category.getId());
    }

    private void saveCategoryImage(Category category, MultipartFile categoryImage) throws IOException, NotAnImageFileException {
        if (categoryImage != null) {
            if(!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(categoryImage.getContentType())) {
                throw new NotAnImageFileException(categoryImage.getOriginalFilename() + NOT_AN_IMAGE_FILE);
            }
            Path userFolder = Paths.get(CATEGORY_FOLDER + category.getName()).toAbsolutePath().normalize();
            if(!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                LOGGER.info(DIRECTORY_CREATED + userFolder);
            }
            Files.deleteIfExists(Paths.get(userFolder + category.getName() + DOT + JPG_EXTENSION));
            Files.copy(categoryImage.getInputStream(), userFolder.resolve(category.getName() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
            category.setCategoryImageUrl(setCategoryImageUrl(category.getName()));
            categoryRepository.save(category);
            LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + categoryImage.getOriginalFilename());
        }
    }

    private String setCategoryImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(CATEGORY_IMAGE_PATH + username + FORWARD_SLASH
                + username + DOT + JPG_EXTENSION).toUriString();
    }

    private String getTemporaryCategoryImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_CATEGORY_IMAGE_PATH + username).toUriString();
    }

    private Category validateNewCategoryName(String currentName, String newName) throws CategoryNameExistException, CategoryNameNotFoundException {
        Category categoryByNewName = findCategoryByName(newName);
            if(StringUtils.isNotBlank(currentName)) {
                Category currentCategory = findCategoryByName(currentName);
                if(currentCategory == null) {
                    throw new CategoryNameNotFoundException(NO_CATEGORY_FOUND_BY_NAME + currentName);
                }
                if(categoryByNewName != null && !currentCategory.getId().equals(categoryByNewName.getId())) {
                    throw new CategoryNameExistException(CATEGORY_NAME_ALREADY_EXISTS);
                }
                return currentCategory;
            } else {
                if(categoryByNewName != null) {
                    throw new CategoryNameExistException(CATEGORY_NAME_ALREADY_EXISTS);
                }
                return null;
            }
    }

}
