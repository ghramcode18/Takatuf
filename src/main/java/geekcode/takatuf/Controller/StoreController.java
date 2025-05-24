package geekcode.takatuf.Controller;

import geekcode.takatuf.dto.store.StoreRequest;
import geekcode.takatuf.dto.store.StoreResponse;
import geekcode.takatuf.Service.StoreService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StoreResponse> createStore(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        StoreRequest request = new StoreRequest();
        request.setName(name);
        request.setDescription(description);
        request.setStatus(status);
        request.setImage(image);

        StoreResponse response = storeService.createStore(userDetails.getUsername(), request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StoreResponse> updateStore(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        StoreRequest request = new StoreRequest();
        request.setName(name);
        request.setDescription(description);
        request.setStatus(status);
        request.setImage(image);

        StoreResponse response = storeService.updateStore(id, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreResponse> getStore(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        StoreResponse response = storeService.getStoreByIdResponse(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteStore(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        storeService.deleteStore(id, userDetails.getUsername());
        return ResponseEntity.ok().body("Store deleted successfully.");
    }

    @GetMapping("/all")
    public ResponseEntity<List<StoreResponse>> getAllStores(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        List<StoreResponse> stores = storeService.getAllStores();
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/mystores")
    public ResponseEntity<List<StoreResponse>> getMyStores(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        List<StoreResponse> myStores = storeService.getStoresByOwnerEmail(userDetails.getUsername());
        return ResponseEntity.ok(myStores);
    }

}
