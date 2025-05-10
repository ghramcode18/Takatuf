package geekcode.takatuf.Service;

import geekcode.takatuf.Entity.*;
import geekcode.takatuf.Exception.Types.ResourceNotFoundException;
import geekcode.takatuf.Repository.*;
import geekcode.takatuf.dto.review.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import geekcode.takatuf.Enums.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

        private final ProductReviewRepository productReviewRepository;
        private final SellerReviewRepository sellerReviewRepository;
        private final ProductRepository productRepository;
        private final UserRepository userRepository;

        public void addProductReview(Long userId, ProductReviewRequest request) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                Product product = productRepository.findById(request.getProductId())
                                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

                ProductReview review = ProductReview.builder()
                                .user(user)
                                .product(product)
                                .rating(request.getRating())
                                .comment(request.getComment())
                                .createdAt(LocalDateTime.now())
                                .build();

                productReviewRepository.save(review);
        }

        public void addSellerReview(Long userId, SellerReviewRequest request) {
                User reviewer = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                User seller = userRepository.findById(request.getSellerId())
                                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

                if (seller.getType() != UserType.SELLER) {
                        throw new IllegalArgumentException("The user being reviewed is not a seller.");
                }

                SellerReview review = SellerReview.builder()
                                .reviewer(reviewer)
                                .seller(seller)
                                .rating(request.getRating())
                                .comment(request.getComment())
                                .createdAt(LocalDateTime.now())
                                .build();

                sellerReviewRepository.save(review);
        }

        public List<SellerReviewResponse> getSellerReviews(Long sellerId) {
                return sellerReviewRepository.findBySeller_Id(sellerId).stream()
                                .map(r -> SellerReviewResponse.builder()
                                                .id(r.getId())
                                                .rating(r.getRating())
                                                .comment(r.getComment())
                                                .reviewerName(r.getReviewer().getName())
                                                .createdAt(r.getCreatedAt())
                                                .build())
                                .collect(Collectors.toList());
        }

        public ProductReviewSummary getProductReviewSummary(Long productId) {
                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new ResourceNotFoundException("product not found"));

                List<ProductReview> reviews = productReviewRepository.findByProduct_Id(productId);

                List<ProductReview> rated = reviews.stream()
                                .filter(r -> r.getRating() != null)
                                .toList();

                List<ProductReview> commentsOnly = reviews.stream()
                                .filter(r -> r.getRating() == null && r.getComment() != null
                                                && !r.getComment().isBlank())
                                .toList();

                double average = rated.stream()
                                .mapToInt(ProductReview::getRating)
                                .average()
                                .orElse(0.0);

                List<ProductReviewResponse> ratedResponses = rated.stream()
                                .map(r -> buildReviewResponse(r))
                                .toList();

                List<ProductReviewResponse> commentOnlyResponses = commentsOnly.stream()
                                .map(r -> buildReviewResponse(r))
                                .toList();

                return ProductReviewSummary.builder()
                                .averageRating(average)
                                .totalReviews(reviews.size())
                                .ratedReviews(ratedResponses)
                                .commentsOnly(commentOnlyResponses)
                                .build();
        }

        private ProductReviewResponse buildReviewResponse(ProductReview r) {
                String userName = (r.getUser() != null) ? r.getUser().getName() : " User not found";
                return ProductReviewResponse.builder()
                                .id(r.getId())
                                .rating(r.getRating())
                                .comment(r.getComment())
                                .userName(userName)
                                .createdAt(r.getCreatedAt())
                                .build();
        }

        public SellerReviewSummary getSellerReviewSummary(Long sellerId) {
                User seller = userRepository.findById(sellerId)
                                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

                List<SellerReview> reviews = sellerReviewRepository.findBySeller_Id(sellerId);

                List<SellerReview> rated = reviews.stream()
                                .filter(r -> r.getRating() != null)
                                .toList();

                List<SellerReview> commentsOnly = reviews.stream()
                                .filter(r -> r.getRating() == null && r.getComment() != null
                                                && !r.getComment().isBlank())
                                .toList();

                double average = rated.stream()
                                .mapToInt(SellerReview::getRating)
                                .average()
                                .orElse(0.0);

                List<SellerReviewResponse> ratedResponses = rated.stream()
                                .map(this::buildReviewResponse)
                                .toList();

                List<SellerReviewResponse> commentOnlyResponses = commentsOnly.stream()
                                .map(this::buildReviewResponse)
                                .toList();

                return SellerReviewSummary.builder()
                                .averageRating(average)
                                .totalReviews(rated.size() + commentOnlyResponses.size())
                                .ratedReviews(ratedResponses)
                                .commentsOnly(commentOnlyResponses)
                                .build();
        }

        private SellerReviewResponse buildReviewResponse(SellerReview r) {
                String reviewerName = (r.getReviewer() != null) ? r.getReviewer().getName() : "User not found";
                return SellerReviewResponse.builder()
                                .id(r.getId())
                                .rating(r.getRating())
                                .comment(r.getComment())
                                .reviewerName(reviewerName)
                                .createdAt(r.getCreatedAt())
                                .build();
        }

}
