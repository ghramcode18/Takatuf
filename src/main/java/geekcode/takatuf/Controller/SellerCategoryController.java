package geekcode.takatuf.Controller;

import geekcode.takatuf.Service.SellerCategoryService;
import geekcode.takatuf.Entity.Category;
import geekcode.takatuf.dto.category.CategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller-categories")
@RequiredArgsConstructor
public class SellerCategoryController {

    private final SellerCategoryService sellerCategoryService;

    @PostMapping("/add")
    public ResponseEntity<Void> addMultipleCategories(
            @RequestBody List<Long> categoryIds,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        sellerCategoryService.addMultipleCategoriesToSeller(getCurrentUserId(userDetails), categoryIds);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeMultipleCategories(
            @RequestBody List<Long> categoryIds,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        sellerCategoryService.removeMultipleCategoriesFromSeller(getCurrentUserId(userDetails), categoryIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<CategoryDto.CategoryResponse>> getMyCategories(
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        List<CategoryDto.CategoryResponse> categories = sellerCategoryService
                .getSellerCategories(getCurrentUserId(userDetails));
        return ResponseEntity.ok(categories);
    }

    private Long getCurrentUserId(UserDetails userDetails) {
        return sellerCategoryService.getUserIdByEmail(userDetails.getUsername());
    }
}
