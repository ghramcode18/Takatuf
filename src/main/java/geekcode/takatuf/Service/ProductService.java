package geekcode.takatuf.Service;

import geekcode.takatuf.Entity.Product;
import geekcode.takatuf.Entity.Store;
import geekcode.takatuf.Repository.ProductRepository;
import geekcode.takatuf.Repository.StoreRepository;
import geekcode.takatuf.dto.product.ProductResponse;
import geekcode.takatuf.Exception.Types.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;

    public ProductResponse addProduct(Long storeId, Product productData) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BadRequestException("Store not found"));

        Product product = Product.builder()
                .name(productData.getName())
                .description(productData.getDescription())
                .piece(productData.getPiece())
                .price(productData.getPrice())
                .image(productData.getImage())
                .category(productData.getCategory())
                .color(productData.getColor())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .store(store)
                .build();

        Product savedProduct = productRepository.save(product);

        return ProductResponse.builder()
                .id(savedProduct.getId())
                .name(savedProduct.getName())
                .description(savedProduct.getDescription())
                .piece(savedProduct.getPiece())
                .price(savedProduct.getPrice())
                .image(savedProduct.getImage())
                .category(savedProduct.getCategory())
                .color(savedProduct.getColor())
                .createdAt(savedProduct.getCreatedAt())
                .updatedAt(savedProduct.getUpdatedAt())

                .storeId(store.getId())
                .storeName(store.getName())
                .build();
    }

    public ProductResponse updateProduct(Long productId, Product productData) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Product not found"));

        product.setName(productData.getName());
        product.setDescription(productData.getDescription());
        product.setPiece(productData.getPiece());
        product.setPrice(productData.getPrice());
        product.setImage(productData.getImage());
        product.setCategory(productData.getCategory());
        product.setColor(productData.getColor());
        product.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);

        return ProductResponse.builder()
                .id(updatedProduct.getId())
                .name(updatedProduct.getName())
                .description(updatedProduct.getDescription())
                .piece(updatedProduct.getPiece())
                .price(updatedProduct.getPrice())
                .image(updatedProduct.getImage())
                .category(updatedProduct.getCategory())
                .color(updatedProduct.getColor())
                .createdAt(updatedProduct.getCreatedAt())
                .updatedAt(updatedProduct.getUpdatedAt())
                .storeId(updatedProduct.getStore().getId())
                .storeName(updatedProduct.getStore().getName())
                .build();
    }

    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Product not found"));

        return buildProductResponse(product);
    }

    public List<ProductResponse> getProductsByStoreId(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BadRequestException("Store not found"));

        List<Product> products = productRepository.findByStoreId(storeId);

        return products.stream()
                .map(this::buildProductResponse)
                .collect(Collectors.toList());
    }

    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new BadRequestException("Product not found");
        }
        productRepository.deleteById(productId);
    }

    private ProductResponse buildProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .piece(product.getPiece())
                .price(product.getPrice())
                .image(product.getImage())
                .category(product.getCategory())
                .color(product.getColor())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .storeId(product.getStore().getId())
                .storeName(product.getStore().getName())
                .build();
    }
}
