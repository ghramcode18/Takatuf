package geekcode.takatuf.Controller;

import geekcode.takatuf.Entity.User;
import geekcode.takatuf.Service.*;
import geekcode.takatuf.dto.product.*;
import geekcode.takatuf.dto.store.*;
import geekcode.takatuf.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import geekcode.takatuf.Exception.Types.*;
import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserRepository userRepository;

    @PostMapping("/products/{productId}")
    public ResponseEntity<Void> addProductToFavorites(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId) {

        Long userId = getUserId(userDetails);
        favoriteService.addProductToFavorites(userId, productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> removeProductFromFavorites(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId) {

        Long userId = getUserId(userDetails);
        favoriteService.removeProductFromFavorites(userId, productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getFavoriteProducts(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(favoriteService.getFavoriteProducts(userId));
    }

    @PostMapping("/stores/{storeId}")
    public ResponseEntity<Void> addStoreToFavorites(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long storeId) {

        Long userId = getUserId(userDetails);
        favoriteService.addStoreToFavorites(userId, storeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/stores/{storeId}")
    public ResponseEntity<Void> removeStoreFromFavorites(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long storeId) {

        Long userId = getUserId(userDetails);
        favoriteService.removeStoreFromFavorites(userId, storeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stores")
    public ResponseEntity<List<StoreResponse>> getFavoriteStores(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(favoriteService.getFavoriteStores(userId));
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow()
                .getId();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearFavorites(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BadRequestException("User not found"));

        favoriteService.removeAllFavorites(user.getId());
        return ResponseEntity.noContent().build();
    }

}
