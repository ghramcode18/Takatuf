package geekcode.takatuf.Service;

import geekcode.takatuf.Entity.Product;
import geekcode.takatuf.Entity.ProductReview;
import geekcode.takatuf.Entity.User;
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
import geekcode.takatuf.dto.*;
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
                if (productRepository.existsByNameAndStoreId(name, storeId)) {
                        throw new BadRequestException("A product with the same name already exists in this store.");
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

        public ProductResponse updateProduct(Long productId, String name, String description, Double price,
                        String category, MultipartFile imageFile, String currentUsername) {

                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new BadRequestException("Product not found"));

                if (!product.getStore().getOwner().getEmail().equals(currentUsername)) {
                        throw new BadRequestException("You are not authorized to update this product");
                }

                if (name != null && !name.isBlank() && !product.getName().equals(name)) {
                        if (productRepository.existsByNameAndStoreId(name, product.getStore().getId())) {
                                throw new BadRequestException(
                                                "Another product with this name already exists in this store.");
                        }
                        product.setName(name);
                }

                if (description != null && !description.isBlank()) {
                        product.setDescription(description);
                }

                if (price != null && price > 0) {
                        product.setPrice(BigDecimal.valueOf(price));
                }

                if (category != null && !category.isBlank()) {
                        try {
                                ProductCategory productCategory = ProductCategory.valueOf(category.toUpperCase());
                                product.setCategory(productCategory);
                        } catch (IllegalArgumentException e) {
                                throw new BadRequestException("Invalid category value: " + category);
                        }
                }

                if (imageFile != null && !imageFile.isEmpty()) {
                        product.setImage(saveImage(imageFile));
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

        public PaginatedResponse<ProductResponse> getProductsByStoreId(Long storeId, int page, int perPage,
                        String q, String sort, String sortDir) {
                Store store = storeRepository.findById(storeId)
                                .orElseThrow(() -> new BadRequestException("Store not found"));

                try {
                        Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? Sort.Direction.DESC
                                        : Sort.Direction.ASC;
                        Pageable pageable = PageRequest.of(Math.max(0, page - 1), perPage, Sort.by(direction, sort));

                        Page<Product> products;
                        if (q != null && !q.trim().isEmpty()) {
                                products = productRepository.findByStoreIdAndNameContainingIgnoreCase(storeId, q,
                                                pageable);
                        } else {
                                products = productRepository.findByStoreId(storeId, pageable);
                        }

                        List<ProductResponse> data = products.map(this::buildProductResponse).getContent();
                        long total = products.getTotalElements();

                        return new PaginatedResponse<>(data, total, page, perPage);

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
                Store store = product.getStore();
                User owner = store.getOwner();
                String sellerImage = owner.getProfileImageUrl();

                List<ProductReview> reviews = product.getProductReviews();
                double avgRating = reviews == null || reviews.isEmpty()
                                ? 0.0
                                : reviews.stream()
                                                .filter(r -> r.getRating() != null)
                                                .mapToDouble(ProductReview::getRating)
                                                .average()
                                                .orElse(0.0);

                return ProductResponse.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .description(product.getDescription())
                                .price(product.getPrice())
                                .image(product.getImage())
                                .category(product.getCategory().name())
                                .createdAt(product.getCreatedAt())
                                .updatedAt(product.getUpdatedAt())

                                .storeId(store.getId())
                                .storeName(store.getName())
                                .storeImage(store.getImageUrl())

                                .sellerName(owner.getName())
                                .sellerImage(sellerImage)

                                .averageRating(avgRating)
                                .build();

        }

        private String saveImage(MultipartFile file) {
                return "https://products/images/" + file.getOriginalFilename();
        }

}