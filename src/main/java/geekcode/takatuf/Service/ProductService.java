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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;

    private final String uploadDir = "uploads/products/";

    public ProductResponse addProduct(Long storeId, String name, String description, BigDecimal price,
            BigDecimal groupDiscountPercentage, Long categoryId, Integer quantity, MultipartFile imageFile,
            String currentUsername) {

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
                .groupDiscountPercentage(groupDiscountPercentage)
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
            BigDecimal groupDiscountPercentage,
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
        if (groupDiscountPercentage != null && groupDiscountPercentage.compareTo(BigDecimal.ZERO) >= 0) {
            product.setGroupDiscountPercentage(groupDiscountPercentage);
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

    public ProductResponse getProductByIdForViewer(Long productId, Long viewerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Product not found"));
        return buildProductResponse(product, viewerId);
    }

    public PaginatedResponse<ProductResponse> getProductsByStoreId(
            Long storeId, int page, int perPage, String q, String sort, String sortDir, Long viewerId) {

        storeRepository.findById(storeId)
                .orElseThrow(() -> new BadRequestException("Store not found"));

        Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), perPage, Sort.by(direction, sort));

        Page<Product> products = (q != null && !q.trim().isEmpty())
                ? productRepository.findByStoreIdAndNameContainingIgnoreCase(storeId, q, pageable)
                : productRepository.findByStoreId(storeId, pageable);

        List<ProductResponse> data = products.getContent().stream()
                .map(p -> buildProductResponse(p, viewerId))
                .toList();

        return new PaginatedResponse<>(data, products.getTotalElements(), page, perPage);
    }

    public List<ProductResponse> getAllProductsByStoreId(Long storeId, Long viewerId) {
        storeRepository.findById(storeId)
                .orElseThrow(() -> new BadRequestException("Store not found"));

        return productRepository.findByStoreId(storeId).stream()
                .map(p -> buildProductResponse(p, viewerId))
                .toList();
    }

    public List<ProductResponse> getProductsByCategoryId(Long categoryId, Long viewerId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(p -> buildProductResponse(p, viewerId))
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

    public List<ProductResponse> searchProducts(String search, List<Long> ids, Long viewerId) {
        List<Product> products;
        boolean hasSearch = search != null && !search.trim().isEmpty();
        boolean hasIds = ids != null && !ids.isEmpty();

        if (hasSearch && hasIds) {
            products = productRepository.findByNameContainingIgnoreCaseAndIdIn(search.trim(), ids);
        } else if (hasSearch) {
            products = productRepository.findByNameContainingIgnoreCase(search.trim());
        } else if (hasIds) {
            products = productRepository.findByIdIn(ids);
        } else {
            return List.of();
        }

        return products.stream().map(p -> buildProductResponse(p, viewerId)).toList();
    }

    private ProductResponse buildProductResponse(Product product, Long viewerId) {
        Store store = product.getStore();
        User owner = store.getOwner();

        List<ProductReview> reviews = product.getProductReviews();
        double avgRating = (reviews == null || reviews.isEmpty())
                ? 0.0
                : reviews.stream()
                        .filter(r -> r.getRating() != null)
                        .mapToDouble(ProductReview::getRating)
                        .average()
                        .orElse(0.0);

        boolean isFav = viewerId != null
                && favoriteRepository.existsByUserIdAndProduct_Id(viewerId, product.getId());

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .groupDiscountPercentage(product.getGroupDiscountPercentage())
                .image(product.getImage())
                .quantity(product.getQuantity())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .storeId(store.getId())
                .storeName(store.getName())
                .storeImage(store.getImageUrl())
                .sellerId(owner.getId())
                .sellerName(owner.getName())
                .sellerImage(owner.getProfileImageUrl())
                .averageRating(avgRating)
                .favorited(isFav)
                .build();
    }

    private ProductResponse buildProductResponse(Product product) {
        return buildProductResponse(product, null);
    }

    public ProductResponse getProductById(Long productId) {
        return getProductByIdForViewer(productId, null);
    }

    private String saveImage(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/products/")
                    .path(fileName)
                    .toUriString();

        } catch (IOException e) {
            throw new BadRequestException("Failed to save product image");
        }

    }
}
