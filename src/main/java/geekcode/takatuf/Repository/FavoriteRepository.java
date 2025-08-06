package geekcode.takatuf.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import geekcode.takatuf.Entity.*;
import geekcode.takatuf.Enums.*;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserIdAndType(Long userId, FavoriteType type);

    boolean existsByUserIdAndProduct_Id(Long userId, Long productId);

    boolean existsByUserIdAndStore_Id(Long userId, Long storeId);

    void deleteByUserIdAndProduct_Id(Long userId, Long productId);

    void deleteByUserIdAndStore_Id(Long userId, Long storeId);

    void deleteByUserId(Long userId);

}
