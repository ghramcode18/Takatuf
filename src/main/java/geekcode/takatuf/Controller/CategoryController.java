package geekcode.takatuf.Controller;

import geekcode.takatuf.Service.CategoryService;
import geekcode.takatuf.dto.PaginatedResponse;
import geekcode.takatuf.dto.category.CategoryDto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoryResponse> createCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute CategoryRequest request) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        CategoryResponse category = categoryService.createCategory(request);
        return ResponseEntity.ok(category);
    }

    @PostMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoryResponse> updateCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @ModelAttribute CategoryRequest request) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        CategoryResponse updated = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/paginated")
    public ResponseEntity<PaginatedResponse<CategoryResponse>> getPaginatedCategories(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int perPage,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "ASC") String sortDir) {

        PaginatedResponse<CategoryResponse> response = categoryService.getCategoriesPaginated(page, perPage, sort,
                sortDir);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        CategoryResponse category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

}
