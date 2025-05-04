package geekcode.takatuf.Controller;

import geekcode.takatuf.Entity.Product;
import geekcode.takatuf.Service.ProductService;
import lombok.RequiredArgsConstructor;
import geekcode.takatuf.dto.product.ProductResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/add/{storeId}")
    public ResponseEntity<ProductResponse> addProduct(
            @PathVariable Long storeId,
            @RequestBody Product productData) {
        ProductResponse response = productService.addProduct(storeId, productData);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @RequestBody Product productData) {
        ProductResponse response = productService.updateProduct(productId, productData);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<ProductResponse>> getProductsByStoreId(@PathVariable Long storeId) {
        List<ProductResponse> products = productService.getProductsByStoreId(storeId);
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().body(
                Map.of("message", "Product deleted successfully"));
    }

}
