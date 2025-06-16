package geekcode.takatuf.Service;

import geekcode.takatuf.dto.store.StoreRequest;
import geekcode.takatuf.dto.store.StoreResponse;
import jakarta.persistence.EntityNotFoundException;
import geekcode.takatuf.Entity.Store;
import geekcode.takatuf.Entity.StoreReview;
import geekcode.takatuf.Entity.User;
import geekcode.takatuf.Exception.Types.BadRequestException;
import geekcode.takatuf.Exception.Types.ResourceNotFoundException;
import geekcode.takatuf.Exception.Types.UnauthorizedException;
import geekcode.takatuf.Repository.StoreRepository;
import geekcode.takatuf.Repository.UserRepository;
import geekcode.takatuf.Repository.StoreRepository;
import geekcode.takatuf.Repository.StoreReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final StoreReviewRepository storeReviewRepository;

    public StoreResponse createStore(String username, StoreRequest request) {
        if (storeRepository.existsByName(request.getName())) {
            throw new BadRequestException("Store name already exists.");
        }

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new BadRequestException("User not found."));

        String imageUrl = saveImage(request.getImage());

        Store store = Store.builder()
                .name(request.getName())
                .description(request.getDescription())
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .owner(user)
                .imageUrl(imageUrl)
                .build();

        Store savedStore = storeRepository.save(store);
        return mapToResponse(savedStore);
    }

    public StoreResponse updateStore(Long storeId, StoreRequest request, String userEmail) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BadRequestException("Store not found."));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BadRequestException("User not found."));

        if (!store.getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not the owner of this store.");
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            boolean nameExists = storeRepository.existsByName(request.getName()) &&
                    !request.getName().equals(store.getName());
            if (nameExists) {
                throw new BadRequestException("Another store with the same name already exists.");
            }
            store.setName(request.getName());
        }

        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            store.setDescription(request.getDescription());
        }

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            store.setStatus(request.getStatus());
        }

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String imageUrl = saveImage(request.getImage());
            store.setImageUrl(imageUrl);
        }

        Store updatedStore = storeRepository.save(store);
        return mapToResponse(updatedStore);
    }

    private String saveImage(MultipartFile image) {
        if (image == null || image.isEmpty())
            return null;

        try {
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }

    public StoreResponse getStoreById(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));
        return mapToResponse(store);
    }

    private StoreResponse mapToResponse(Store store) {
        List<StoreReview> reviews = storeReviewRepository.findByStore_Id(store.getId());
        double averageRating = reviews.stream()
                .mapToInt(StoreReview::getRating)
                .average()
                .orElse(0.0);

        return StoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .description(store.getDescription())
                .status(store.getStatus())
                .imageUrl(store.getImageUrl())
                .ownerEmail(store.getOwner().getEmail())
                .ownerName(store.getOwner().getName())
                .averageRating(averageRating)
                .totalReviews(reviews.size())
                .build();
    }

    public void deleteStore(Long storeId, String userEmail) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BadRequestException("Store not found."));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BadRequestException("User not found."));

        if (!store.getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not the owner of this store.");
        }

        storeRepository.delete(store);
    }

    public List<StoreResponse> getAllStores() {
        List<Store> stores = storeRepository.findAll();
        return stores.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<StoreResponse> getStoresByOwnerEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found."));
        List<Store> stores = storeRepository.findByOwner_Id(user.getId());
        return stores.stream()
                .map(this::mapToResponse)
                .toList();
    }

}
