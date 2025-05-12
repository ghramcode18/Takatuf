package geekcode.takatuf.Service;

import geekcode.takatuf.dto.store.StoreRequest;
import geekcode.takatuf.dto.store.StoreResponse;
import jakarta.persistence.EntityNotFoundException;
import geekcode.takatuf.Entity.Store;
import geekcode.takatuf.Entity.User;
import geekcode.takatuf.Exception.Types.BadRequestException;
import geekcode.takatuf.Repository.StoreRepository;
import geekcode.takatuf.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    public StoreResponse createStore(String username, StoreRequest request) {
        if (storeRepository.existsByName(request.getName())) {
            throw new BadRequestException("Store name already exists.");
        }

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new BadRequestException("User not found."));

        Store store = Store.builder()
                .name(request.getName())
                .description(request.getDescription())
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .owner(user)
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
            throw new BadRequestException("You are not the owner of this store.");
        }

        store.setName(request.getName());
        store.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            store.setStatus(request.getStatus());
        }

        Store updatedStore = storeRepository.save(store);
        return mapToResponse(updatedStore);
    }

    public Store getStoreById(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Store not found"));
    }

    public StoreResponse getStoreByIdResponse(Long id) {
        return mapToResponse(getStoreById(id));
    }

    private StoreResponse mapToResponse(Store store) {
        return StoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .description(store.getDescription())
                .status(store.getStatus())
                .ownerEmail(store.getOwner().getEmail())
                .ownerName(store.getOwner().getName())
                .build();
    }

    public void deleteStore(Long storeId, String userEmail) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BadRequestException("Store not found."));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BadRequestException("User not found."));

        if (!store.getOwner().getId().equals(user.getId())) {
            throw new BadRequestException("You are not the owner of this store.");
        }

        storeRepository.delete(store);
    }

    public List<StoreResponse> getAllStores() {
        List<Store> stores = storeRepository.findAll();
        return stores.stream()
                .map(this::mapToResponse)
                .toList();
    }

}
