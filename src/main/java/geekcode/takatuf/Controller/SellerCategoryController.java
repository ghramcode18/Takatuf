package geekcode.takatuf.Controller;

import geekcode.takatuf.Service.SellerCategoryService;
import geekcode.takatuf.Entity.Category;
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
    public ResponseEntity<Void> addCategory(
            @RequestParam Long categoryId,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        sellerCategoryService.addCategoryToSeller(getCurrentUserId(userDetails), categoryId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeCategory(
            @RequestParam Long categoryId,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        sellerCategoryService.removeCategoryFromSeller(getCurrentUserId(userDetails), categoryId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<Category>> getMyCategories(
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        List<Category> categories = sellerCategoryService.getSellerCategories(getCurrentUserId(userDetails));
        return ResponseEntity.ok(categories);
    }

    private Long getCurrentUserId(UserDetails userDetails) {
        return sellerCategoryService.getUserIdByEmail(userDetails.getUsername());
    }
}
