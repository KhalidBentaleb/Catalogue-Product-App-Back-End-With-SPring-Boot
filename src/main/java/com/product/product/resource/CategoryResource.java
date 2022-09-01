package com.product.product.resource;

import com.product.product.domain.Category;
import com.product.product.domain.HttpResponse;
import com.product.product.exception.ExceptionHandling;
import com.product.product.exception.domain.*;
import com.product.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.product.product.constant.FileConstant.CATEGORY_FOLDER;
import static com.product.product.constant.FileConstant.FORWARD_SLASH;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping(path = { "/", "/category"})
public class CategoryResource extends ExceptionHandling {
    public static final String CATEGORY_DELETED_SUCCESSFULLY = "Category deleted successfully";
    private CategoryService categoryService;

    @Autowired
    public CategoryResource(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/add")
    public ResponseEntity<Category> addNewCategory(@RequestParam("name") String name,
                                                   @RequestParam(value = "categoryImage", required = false) MultipartFile categoryImage) throws IOException, CategoryNameExistException, CategoryNameNotFoundException, NotAnImageFileException {
        Category newCategory = categoryService.addNewCategory(name, categoryImage);
        return new ResponseEntity<>(newCategory, OK);
    }

    @PostMapping("/update")
    public ResponseEntity<Category> updateCategory(@RequestParam("currentName") String currentName,
                                           @RequestParam("name") String name,
                                           @RequestParam(value = "categoryImage", required = false) MultipartFile categoryImage) throws CategoryNameExistException, CategoryNameNotFoundException, IOException, NotAnImageFileException {
        Category updatedCategory = categoryService.updateCategory(currentName, name, categoryImage);
        return new ResponseEntity<>(updatedCategory, OK);
    }

    @GetMapping("/find/{name}")
    public ResponseEntity<Category> getCategory(@PathVariable("name") String name) {
        Category category = categoryService.findCategoryByName(name);
        return new ResponseEntity<>(category, OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getCategories();
        return new ResponseEntity<>(categories, OK);
    }

    @DeleteMapping("/delete/{name}")
    public ResponseEntity<HttpResponse> deleteCategory(@PathVariable("name") String name) throws IOException {
        categoryService.deleteCategory(name);
        return response(OK, CATEGORY_DELETED_SUCCESSFULLY);
    }

    @GetMapping(path = "/image/{name}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getCategoryImage(@PathVariable("name") String name, @PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(CATEGORY_FOLDER + name + FORWARD_SLASH + fileName));
    }

    @PostMapping("/updateCategoryImage")
    public ResponseEntity<Category> updateCategoryImage(@RequestParam("name") String name, @RequestParam(value = "categoryImage") MultipartFile categoryImage) throws IOException, NotAnImageFileException, CategoryNameNotFoundException, CategoryNameExistException {
        Category category = categoryService.updateCategoryImage(name, categoryImage);
        return new ResponseEntity<>(category, OK);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }
}
