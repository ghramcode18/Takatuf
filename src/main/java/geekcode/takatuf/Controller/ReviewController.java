package geekcode.takatuf.Controller;

import geekcode.takatuf.Entity.User;
import geekcode.takatuf.Service.ReviewService;
import geekcode.takatuf.Repository.UserRepository;
import geekcode.takatuf.dto.MessageResponse;
import geekcode.takatuf.dto.review.*;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserRepository userRepository;

    @PostMapping("/product")
    public ResponseEntity<MessageResponse> addProductReview(
            @RequestBody ProductReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = getAuthenticatedUser(userDetails);
        reviewService.addProductReview(user.getId(), request);
        return ResponseEntity.ok(new MessageResponse("Product review added successfully"));
    }

    @PostMapping("/seller")
    public ResponseEntity<MessageResponse> addSellerReview(
            @RequestBody SellerReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = getAuthenticatedUser(userDetails);
        reviewService.addSellerReview(user.getId(), request);
        return ResponseEntity.ok(new MessageResponse("Seller review added successfully"));
    }

    @PostMapping("/store")
    public ResponseEntity<MessageResponse> addStoreReview(
            @RequestBody StoreReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = getAuthenticatedUser(userDetails);
        reviewService.addStoreReview(user.getId(), request);
        return ResponseEntity.ok(new MessageResponse("Store review added successfully"));
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<SellerReviewResponse>> getSellerReviews(
            @PathVariable Long sellerId,
            @AuthenticationPrincipal UserDetails userDetails) {

        getAuthenticatedUser(userDetails);
        return ResponseEntity.ok(reviewService.getSellerReviews(sellerId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductReviewResponse>> getProductReviews(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails) {

        getAuthenticatedUser(userDetails);

        return ResponseEntity.ok(reviewService.getProductReviews(productId));
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<StoreReviewResponse>> getStoreReviews(
            @PathVariable Long storeId,
            @AuthenticationPrincipal UserDetails userDetails) {

        getAuthenticatedUser(userDetails);
        return ResponseEntity.ok(reviewService.getStoreReviews(storeId));
    }

    @GetMapping("/products/{productId}/summary")
    public ResponseEntity<ProductReviewSummary> getProductReviewSummary(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails) {

        getAuthenticatedUser(userDetails);
        return ResponseEntity.ok(reviewService.getProductReviewSummary(productId));
    }

    @GetMapping("/sellers/{sellerId}/summary")
    public ResponseEntity<SellerReviewSummary> getSellerSummary(
            @PathVariable Long sellerId,
            @AuthenticationPrincipal UserDetails userDetails) {

        getAuthenticatedUser(userDetails);
        return ResponseEntity.ok(reviewService.getSellerReviewSummary(sellerId));
    }

    private User getAuthenticatedUser(UserDetails userDetails) {
        if (userDetails == null) {
            throw new RuntimeException("Unauthorized access");
        }

        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    @GetMapping("/stores/{storeId}/summary")
    public ResponseEntity<StoreReviewSummary> getStoreReviewSummary(
            @PathVariable Long storeId,
            @AuthenticationPrincipal UserDetails userDetails) {

        getAuthenticatedUser(userDetails);
        return ResponseEntity.ok(reviewService.getStoreReviewSummary(storeId));
    }

}
