package geekcode.takatuf.Controller;

import geekcode.takatuf.Service.ProductService;
import geekcode.takatuf.dto.PaginatedResponse;
import geekcode.takatuf.dto.product.ProductResponse;
import geekcode.takatuf.dto.product.ProductSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/add/{storeId}")
    public ResponseEntity<ProductResponse> addProduct(
            @PathVariable Long storeId,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam BigDecimal price,
            @RequestParam BigDecimal groupDiscountPercentage,
            @RequestParam Long categoryId,
            @RequestParam Integer quantity,
            @RequestParam MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null)
            return ResponseEntity.status(401).build();

        ProductResponse response = productService.addProduct(
                storeId,
                name,
                description,
                price,
                groupDiscountPercentage,
                categoryId,
                quantity,
                image,
                userDetails.getUsername());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/update/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) BigDecimal groupDiscountPercentage,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null)
            return ResponseEntity.status(401).build();

        ProductResponse response = productService.updateProduct(
                productId,
                name,
                description,
                price,
                groupDiscountPercentage,
                categoryId,
                quantity,
                image,
                userDetails.getUsername());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null)
            return ResponseEntity.status(401).build();

        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/store/{storeId}/products")
    public ResponseEntity<PaginatedResponse<ProductResponse>> getProducts(
            @PathVariable Long storeId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "per_page", defaultValue = "10") int perPage,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "ASC") String sortDir,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null)
            return ResponseEntity.status(401).build();

        return ResponseEntity.ok(productService.getProductsByStoreId(
                storeId, page, perPage, q, sort, sortDir));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null)
            return ResponseEntity.status(401).build();

        productService.deleteProduct(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/store/{storeId}/all-products")
    public ResponseEntity<List<ProductResponse>> getAllStoreProducts(
            @PathVariable Long storeId,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null)
            return ResponseEntity.status(401).build();

        return ResponseEntity.ok(productService.getAllProductsByStoreId(storeId));
    }

    @GetMapping("/category/{categoryId}/products")
    public ResponseEntity<List<ProductResponse>> getProductsByCategoryId(
            @PathVariable Long categoryId) {

        return ResponseEntity.ok(productService.getProductsByCategoryId(categoryId));
    }

    @PostMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestBody ProductSearchRequest request) {
        List<ProductResponse> results = productService.searchProducts(request.getSearch());
        return ResponseEntity.ok(results);
    }

}
