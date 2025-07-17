package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.SellerCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SellerCategoryRepository extends JpaRepository<SellerCategory, Long> {
    List<SellerCategory> findBySeller_Id(Long sellerId);

    boolean existsBySellerIdAndCategoryId(Long sellerId, Long categoryId);

    void deleteBySellerId(Long sellerId);
}
