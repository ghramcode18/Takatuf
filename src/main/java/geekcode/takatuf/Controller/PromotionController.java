package geekcode.takatuf.Controller;

import geekcode.takatuf.Service.PromotionService;
import geekcode.takatuf.dto.PromotionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {
    private final PromotionService promotionService;

    @PostMapping("/request")
    public ResponseEntity<PromotionResponse> requestPromotion(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long productId,
            @RequestParam Integer durationDays) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        PromotionResponse promotion = promotionService.createRequest(
                userDetails.getUsername(),
                productId,
                durationDays);

        return ResponseEntity.ok(promotion);
    }

    @GetMapping("/my")
    public ResponseEntity<?> myRequests(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "per_page", defaultValue = "10") int perPage) {
        if (userDetails == null)
            return ResponseEntity.status(401).build();

        return ResponseEntity.ok(
                promotionService.getSellerRequests(userDetails.getUsername(), page, perPage));
    }

    @GetMapping("/admin/pending")
    public ResponseEntity<?> pendingForAdmin(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "per_page", defaultValue = "10") int perPage) {
        return ResponseEntity.ok(promotionService.getPendingForAdmin(page, perPage));
    }

    @PostMapping("/admin/review/{id}")
    public ResponseEntity<Void> review(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam boolean approve,
            @RequestParam(required = false) String startAt) {
        if (userDetails == null)
            return ResponseEntity.status(401).build();

        LocalDateTime start = (startAt != null && !startAt.isBlank())
                ? LocalDateTime.parse(startAt)
                : null;

        promotionService.review(id, userDetails.getUsername(), approve, start);
        return ResponseEntity.noContent().build();
    }
}
