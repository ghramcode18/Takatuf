package geekcode.takatuf.Service;

import geekcode.takatuf.Entity.*;
import geekcode.takatuf.Exception.Types.ResourceNotFoundException;
import geekcode.takatuf.Repository.*;
import geekcode.takatuf.dto.*;
import geekcode.takatuf.dto.product.ProductResponse;
import geekcode.takatuf.dto.store.StoreResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import geekcode.takatuf.Enums.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final ProductService productService;
    private final StoreService storeService;
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public void addProductToFavorites(Long userId, Long productId) {
        if (favoriteRepository.existsByUserIdAndProduct_Id(userId, productId))
            return;

        User user = userRepository.findById(userId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);
        favorite.setType(FavoriteType.PRODUCT);
        favorite.setCreatedAt(LocalDateTime.now());

        favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeProductFromFavorites(Long userId, Long productId) {
        favoriteRepository.deleteByUserIdAndProduct_Id(userId, productId);
    }

    public List<ProductResponse> getFavoriteProducts(Long userId) {
        return favoriteRepository.findByUserIdAndType(userId, FavoriteType.PRODUCT)
                .stream()
                .map(Favorite::getProduct)
                .map(p -> productService.getProductByIdForViewer(p.getId(), userId))
                .toList();
    }

    @Transactional
    public void addStoreToFavorites(Long userId, Long storeId) {
        if (favoriteRepository.existsByUserIdAndStore_Id(userId, storeId))
            return;

        User user = userRepository.findById(userId).orElseThrow();
        Store store = storeRepository.findById(storeId).orElseThrow();

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setStore(store);
        favorite.setType(FavoriteType.STORE);
        favorite.setCreatedAt(LocalDateTime.now());

        favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeStoreFromFavorites(Long userId, Long storeId) {
        favoriteRepository.deleteByUserIdAndStore_Id(userId, storeId);
    }

    public List<StoreResponse> getFavoriteStores(Long userId) {
        return favoriteRepository.findByUserIdAndType(userId, FavoriteType.STORE)
                .stream()
                .map(Favorite::getStore)
                .map(s -> storeService.getStoreByIdForViewer(s.getId(), userId))
                .toList();
    }

    @Transactional
    public void removeAllFavorites(Long userId) {

        favoriteRepository.deleteByUserId(userId);
    }

}
