package geekcode.takatuf.Controller;

import geekcode.takatuf.Entity.User;
import geekcode.takatuf.Service.ReviewService;
import geekcode.takatuf.Repository.UserRepository;
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
    public ResponseEntity<Void> addProductReview(
            @RequestBody ProductReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = getAuthenticatedUser(userDetails);
        reviewService.addProductReview(user.getId(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/seller")
    public ResponseEntity<Void> addSellerReview(
            @RequestBody SellerReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = getAuthenticatedUser(userDetails);
        reviewService.addSellerReview(user.getId(), request);
        return ResponseEntity.ok().build();
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
}
