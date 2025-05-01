package geekcode.takatuf.Controller;

import geekcode.takatuf.dto.store.StoreRequest;
import geekcode.takatuf.dto.store.StoreResponse;
import geekcode.takatuf.Entity.Store;
import geekcode.takatuf.Service.StoreService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/add")
    public ResponseEntity<StoreResponse> createStore(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody StoreRequest request) {

        String email = userDetails.getUsername();
        StoreResponse response = storeService.createStore(email, request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<StoreResponse> updateStore(
            @PathVariable Long id,
            @Valid @RequestBody StoreRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        StoreResponse response = storeService.updateStore(id, request, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreResponse> getStore(@PathVariable Long id) {
        StoreResponse response = storeService.getStoreByIdResponse(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteStore(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        storeService.deleteStore(id, userDetails.getUsername());
        return ResponseEntity.ok().body("Store deleted successfully.");
    }

}
