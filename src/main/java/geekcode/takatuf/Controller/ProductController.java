package geekcode.takatuf.Controller;

import geekcode.takatuf.Service.ProductService;
import geekcode.takatuf.dto.MessageResponse;
import geekcode.takatuf.dto.PaginatedResponse;
import geekcode.takatuf.dto.product.ProductResponse;
import geekcode.takatuf.Repository.ProductRepository;
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
    private final ProductRepository productRepository;

    @PostMapping("/add/{storeId}")
    public ResponseEntity<ProductResponse> addProduct(
            @PathVariable Long storeId,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam("category") Long categoryId,
            @RequestParam("quantity") Integer quantity,
            @RequestParam("image") MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null)
            return ResponseEntity.status(401).build();

        ProductResponse response = productService.addProduct(
                storeId, name, description, BigDecimal.valueOf(price),
                categoryId, quantity, image, userDetails.getUsername());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/update/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam(value = "price", required = false) BigDecimal price,
            @RequestParam(value = "quantity", required = false) Integer quantity,
            @RequestParam(value = "category", required = false) Long categoryId,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null)
            return ResponseEntity.status(401).build();

        ProductResponse response = productService.updateProduct(
                productId, name, description, price, categoryId, quantity, image, userDetails.getUsername());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null)
            return ResponseEntity.status(401).build();

        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
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

        PaginatedResponse<ProductResponse> result = productService.getProductsByStoreId(
                storeId, page, perPage, q, sort, sortDir);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<MessageResponse> deleteProduct(
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

        List<ProductResponse> products = productService.getAllProductsByStoreId(storeId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{categoryId}/products")
    public ResponseEntity<List<ProductResponse>> getProductsByCategoryId(@PathVariable Long id) {
        List<ProductResponse> products = productService.getProductsByCategoryId(id);
        return ResponseEntity.ok(products);
    }

}
