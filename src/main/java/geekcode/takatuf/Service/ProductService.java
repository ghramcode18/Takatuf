package geekcode.takatuf.Service;

import geekcode.takatuf.Entity.*;
import geekcode.takatuf.Exception.Types.BadRequestException;
import geekcode.takatuf.Repository.*;
import geekcode.takatuf.dto.PaginatedResponse;
import geekcode.takatuf.dto.product.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;

    public ProductResponse addProduct(Long storeId, String name, String description, BigDecimal price,
            Long categoryId, Integer quantity, MultipartFile imageFile, String currentUsername) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BadRequestException("Store not found"));

        if (!store.getOwner().getEmail().equals(currentUsername)) {
            throw new BadRequestException("You are not authorized to add products to this store");
        }

        if (productRepository.existsByNameAndStoreId(name, storeId)) {
            throw new BadRequestException("A product with the same name already exists in this store.");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BadRequestException("Category not found"));

        String imageUrl = saveImage(imageFile);

        Product product = Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .image(imageUrl)
                .quantity(quantity)
                .category(category)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .store(store)
                .build();

        return buildProductResponse(productRepository.save(product));
    }

    public ProductResponse updateProduct(Long productId, String name, String description, BigDecimal price,
            Long categoryId, Integer quantity, MultipartFile imageFile, String currentUsername) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Product not found"));

        validateOwnership(product, currentUsername);

        if (name != null && !name.isBlank() && !product.getName().equals(name)) {
            if (productRepository.existsByNameAndStoreId(name, product.getStore().getId())) {
                throw new BadRequestException("Another product with this name already exists in this store.");
            }
            product.setName(name);
        }

        if (description != null && !description.isBlank()) {
            product.setDescription(description);
        }

        if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
            product.setPrice(price);
        }

        if (quantity != null && quantity >= 0) {
            product.setQuantity(quantity);
        }

        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BadRequestException("Category not found"));
            product.setCategory(category);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            product.setImage(saveImage(imageFile));
        }

        product.setUpdatedAt(LocalDateTime.now());
        return buildProductResponse(productRepository.save(product));
    }

    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Product not found"));
        return buildProductResponse(product);
    }

    public PaginatedResponse<ProductResponse> getProductsByStoreId(Long storeId, int page, int perPage,
            String q, String sort, String sortDir) {

        storeRepository.findById(storeId)
                .orElseThrow(() -> new BadRequestException("Store not found"));

        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(Math.max(0, page - 1), perPage, Sort.by(direction, sort));

            Page<Product> products = (q != null && !q.trim().isEmpty())
                    ? productRepository.findByStoreIdAndNameContainingIgnoreCase(storeId, q, pageable)
                    : productRepository.findByStoreId(storeId, pageable);

            List<ProductResponse> data = products.map(this::buildProductResponse).getContent();

            return new PaginatedResponse<>(data, products.getTotalElements(), page, perPage);

        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid sort field: " + sort);
        }
    }

    public List<ProductResponse> getAllProductsByStoreId(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BadRequestException("Store not found"));

        List<Product> products = productRepository.findByStoreId(storeId);

        return products.stream()
                .map(this::buildProductResponse)
                .toList();
    }

    public void deleteProduct(Long productId, String currentUsername) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Product not found"));

        validateOwnership(product, currentUsername);

        productRepository.deleteById(productId);
    }

    private void validateOwnership(Product product, String currentUsername) {
        if (!product.getStore().getOwner().getEmail().equals(currentUsername)) {
            throw new BadRequestException("You are not authorized to perform this action");
        }
    }

    public List<ProductResponse> getProductsByCategoryId(Long categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        return products.stream()
                .map(this::buildProductResponse)
                .toList();
    }
    private ProductResponse buildProductResponse(Product product) {
        Store store = product.getStore();
        User owner = store.getOwner();
        String sellerImage = owner.getProfileImageUrl();

        List<ProductReview> reviews = product.getProductReviews();
        double avgRating = (reviews == null || reviews.isEmpty())
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
                .quantity(product.getQuantity())
                .category(product.getCategory().getName())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .storeId(store.getId())
                .storeName(store.getName())
                .storeImage(store.getImageUrl())
                .sellerId(owner.getId())
                .sellerName(owner.getName())
                .sellerImage(sellerImage)
                .averageRating(avgRating)
                .build();
    }

    private String saveImage(MultipartFile file) {
        return "https://products/images/" + file.getOriginalFilename();
    }
}
