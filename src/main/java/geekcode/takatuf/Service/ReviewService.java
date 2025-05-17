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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

        private final ProductReviewRepository productReviewRepository;
        private final SellerReviewRepository sellerReviewRepository;
        private final ProductRepository productRepository;
        private final UserRepository userRepository;

        @Transactional
        public void addProductReview(Long userId, ProductReviewRequest request) {
                boolean hasRating = request.getRating() != null;
                boolean hasComment = request.getComment() != null && !request.getComment().isBlank();

                if (!hasRating && !hasComment) {
                        throw new IllegalArgumentException("Either rating or comment must be provided.");
                }

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                Product product = productRepository.findById(request.getProductId())
                                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

                productReviewRepository.findAllByUser_IdAndProduct_Id(userId, request.getProductId())
                                .ifPresentOrElse(existingReview -> {
                                        if (hasRating)
                                                existingReview.setRating(request.getRating());
                                        if (hasComment)
                                                existingReview.setComment(request.getComment());
                                        existingReview.setCreatedAt(LocalDateTime.now());
                                        productReviewRepository.save(existingReview);
                                }, () -> {
                                        ProductReview review = ProductReview.builder()
                                                        .user(user)
                                                        .product(product)
                                                        .rating(hasRating ? request.getRating() : null)
                                                        .comment(hasComment ? request.getComment() : null)
                                                        .createdAt(LocalDateTime.now())
                                                        .build();

                                        productReviewRepository.save(review);
                                });
        }

        @Transactional
        public void addSellerReview(Long userId, SellerReviewRequest request) {
                boolean hasRating = request.getRating() != null;
                boolean hasComment = request.getComment() != null && !request.getComment().isBlank();

                if (!hasRating && !hasComment) {
                        throw new IllegalArgumentException("Either rating or comment must be provided.");
                }

                User reviewer = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                User seller = userRepository.findById(request.getSellerId())
                                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

                if (seller.getType() != UserType.SELLER) {
                        throw new IllegalArgumentException("The user being reviewed is not a seller.");
                }

                sellerReviewRepository.findByReviewer_IdAndSeller_Id(userId, request.getSellerId())
                                .ifPresentOrElse(existingReview -> {
                                        if (hasRating)
                                                existingReview.setRating(request.getRating());
                                        if (hasComment)
                                                existingReview.setComment(request.getComment());
                                        existingReview.setCreatedAt(LocalDateTime.now());
                                        sellerReviewRepository.save(existingReview);
                                }, () -> {
                                        SellerReview review = SellerReview.builder()
                                                        .reviewer(reviewer)
                                                        .seller(seller)
                                                        .rating(hasRating ? request.getRating() : null)
                                                        .comment(hasComment ? request.getComment() : null)
                                                        .createdAt(LocalDateTime.now())
                                                        .build();

                                        sellerReviewRepository.save(review);
                                });
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

        public List<ProductReviewResponse> getProductReviews(Long productId) {
                productRepository.findById(productId)
                                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

                List<ProductReview> reviews = productReviewRepository.findByProduct_Id(productId);

                return reviews.stream()
                                .filter(r -> r.getRating() != null && r.getComment() != null
                                                && !r.getComment().isBlank())
                                .map(this::buildReviewResponse)
                                .collect(Collectors.toList());
        }

        public ProductReviewSummary getProductReviewSummary(Long productId) {
                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new ResourceNotFoundException("product not found"));

                List<ProductReview> reviews = productReviewRepository.findByProduct_Id(productId);

                List<ProductReview> validReviews = reviews.stream()
                                .filter(r -> r.getRating() != null &&
                                                r.getComment() != null &&
                                                !r.getComment().isBlank())
                                .toList();

                double average = validReviews.stream()
                                .mapToInt(ProductReview::getRating)
                                .average()
                                .orElse(0.0);

                List<ProductReviewResponse> responses = validReviews.stream()
                                .map(this::buildReviewResponse)
                                .toList();

                return ProductReviewSummary.builder()
                                .averageRating(average)
                                .totalReviews(validReviews.size())
                                .ratedReviews(responses)
                                .build();
        }

        private ProductReviewResponse buildReviewResponse(ProductReview r) {
                String userName = (r.getUser() != null) ? r.getUser().getName() : "User not found";
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

                double average = reviews.stream()
                                .mapToInt(SellerReview::getRating)
                                .average()
                                .orElse(0.0);

                List<SellerReviewResponse> responses = reviews.stream()
                                .map(this::buildReviewResponse)
                                .toList();

                return SellerReviewSummary.builder()
                                .averageRating(average)
                                .totalReviews(reviews.size())
                                .ratedReviews(responses)
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
