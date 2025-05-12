package geekcode.takatuf.Service;

import geekcode.takatuf.Entity.Product;
import geekcode.takatuf.Entity.Store;
import geekcode.takatuf.Repository.ProductRepository;
import geekcode.takatuf.Repository.StoreRepository;
import geekcode.takatuf.dto.product.ProductResponse;
import geekcode.takatuf.Exception.Types.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

        public Page<ProductResponse> getProductsByStoreId(
                        Long storeId,
                        int page,
                        int perPage,
                        String q,
                        String sort,
                        String sortDir) {

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
