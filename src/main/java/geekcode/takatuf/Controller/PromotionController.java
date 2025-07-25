package geekcode.takatuf.Controller;

import geekcode.takatuf.Service.PromotionService;
import geekcode.takatuf.dto.promotion.PromotionRequest;
import geekcode.takatuf.dto.promotion.PromotionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<PromotionResponse> createPromotion(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute PromotionRequest request) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        PromotionResponse response = promotionService.createPromotion(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/update/{id}", consumes = "multipart/form-data")
    public ResponseEntity<PromotionResponse> updatePromotion(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute PromotionRequest request) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        PromotionResponse response = promotionService.updatePromotion(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PromotionResponse>> getAllPromotions() {
        return ResponseEntity.ok(promotionService.getAllPromotions());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<PromotionResponse>> getPromotionsByActive(
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(promotionService.getPromotionsByActiveStatus(active));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromotionResponse> getPromotionById(@PathVariable Long id) {
        return ResponseEntity.ok(promotionService.getPromotionById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        promotionService.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }
}
