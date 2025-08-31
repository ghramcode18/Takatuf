package geekcode.takatuf.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import geekcode.takatuf.Entity.*;
import geekcode.takatuf.Enums.*;
import java.util.List;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductPromotionRepository extends JpaRepository<ProductPromotion, Long> {
    Page<ProductPromotion> findBySeller_Id(Long sellerId, Pageable pageable);

    Page<ProductPromotion> findByStatus(PromotionStatus status, Pageable pageable);

    boolean existsByProduct_IdAndStatus(Long productId, PromotionStatus status);

    @Query("""
                select p from ProductPromotion p
                where p.product.id = :productId
                  and p.status = 'APPROVED'
                  and p.startAt <= :now and p.endAt >= :now
            """)
    List<ProductPromotion> findActiveByProduct(@Param("productId") Long productId, @Param("now") LocalDateTime now);

    boolean existsByProduct_IdAndEndAtAfter(Long productId, LocalDateTime now);
}
