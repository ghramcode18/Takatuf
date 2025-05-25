package geekcode.takatuf.Service;

import geekcode.takatuf.Entity.Product;
import geekcode.takatuf.Entity.Store;
import geekcode.takatuf.Enums.ProductCategory;
import geekcode.takatuf.Exception.Types.BadRequestException;
import geekcode.takatuf.Repository.ProductRepository;
import geekcode.takatuf.Repository.StoreRepository;
import geekcode.takatuf.dto.product.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

        private final ProductRepository productRepository;
        private final StoreRepository storeRepository;

        public ProductResponse addProduct(Long storeId, String name, String description, BigDecimal price,
                        ProductCategory category, MultipartFile imageFile, String currentUsername) {

                Store store = storeRepository.findById(storeId)
                                .orElseThrow(() -> new BadRequestException("Store not found"));

                if (!store.getOwner().getEmail().equals(currentUsername)) {
                        throw new BadRequestException("You are not authorized to add products to this store");
                }

                String imageUrl = saveImage(imageFile);

                Product product = Product.builder()
                                .name(name)
                                .description(description)
                                .price(price)
                                .image(imageUrl)
                                .category(category)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .store(store)
                                .build();

                Product savedProduct = productRepository.save(product);
                return buildProductResponse(savedProduct);
        }

        public ProductResponse updateProduct(Long productId, String name, String description, double price,
                        ProductCategory category, MultipartFile imageFile, String currentUsername) {

                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new BadRequestException("Product not found"));
                if (!product.getStore().getOwner().getEmail().equals(currentUsername)) {
                        throw new BadRequestException("You are not authorized to update this product");
                }
                product.setName(name);
                product.setDescription(description);
                product.setPrice(BigDecimal.valueOf(price));
                product.setCategory(category);
                if (imageFile != null && !imageFile.isEmpty()) {
                        product.setImage(saveImage(imageFile)); // Stub — replace with your logic
                }
                product.setUpdatedAt(LocalDateTime.now());

                Product updatedProduct = productRepository.save(product);
                return buildProductResponse(updatedProduct);
        }

        public ProductResponse getProductById(Long productId) {
                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new BadRequestException("Product not found"));

                return buildProductResponse(product);
        }

        public Page<ProductResponse> getProductsByStoreId(Long storeId, int page, int perPage,
                        String q, String sort, String sortDir) {
                Store store = storeRepository.findById(storeId)
                                .orElseThrow(() -> new BadRequestException("Store not found"));

                try {
                        Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? Sort.Direction.DESC
                                        : Sort.Direction.ASC;
                        Pageable pageable = PageRequest.of(page, perPage, Sort.by(direction, sort));

                        Page<Product> products;
                        if (q != null && !q.trim().isEmpty()) {
                                products = productRepository.findByStoreIdAndNameContainingIgnoreCase(storeId, q,
                                                pageable);
                        } else {
                                products = productRepository.findByStoreId(storeId, pageable);
                        }

                        return products.map(this::buildProductResponse);

                } catch (IllegalArgumentException e) {
                        throw new BadRequestException("Invalid sort field: " + sort);
                }
        }

        public void deleteProduct(Long productId, String currentUsername) {
                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new BadRequestException("Product not found"));

                if (!product.getStore().getOwner().getEmail().equals(currentUsername)) {
                        throw new BadRequestException("You are not authorized to delete this product");
                }

                productRepository.deleteById(productId);
        }

        public ProductResponse buildProductResponse(Product product) {
                return ProductResponse.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .description(product.getDescription())
                                .price(product.getPrice())
                                .image(product.getImage())
                                .category(product.getCategory().name())
                                .createdAt(product.getCreatedAt())
                                .updatedAt(product.getUpdatedAt())
                                .storeId(product.getStore().getId())
                                .storeName(product.getStore().getName())
                                .build();

        }

        private String saveImage(MultipartFile file) {
                return "https://products/images/" + file.getOriginalFilename();
        }

}