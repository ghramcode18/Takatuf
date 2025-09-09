package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.Product;
import geekcode.takatuf.Entity.ProductPromotion;
import geekcode.takatuf.Enums.PromotionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ProductPromotionRepository extends JpaRepository<ProductPromotion, Long> {

  Page<ProductPromotion> findBySeller_Id(Long sellerId, Pageable pageable);

  Page<ProductPromotion> findByStatus(PromotionStatus status, Pageable pageable);

  boolean existsByProduct_IdAndStatus(Long productId, PromotionStatus status);

  @Query("""
      select (count(p) > 0) from ProductPromotion p
      where p.product.id = :productId
        and p.status = geekcode.takatuf.Enums.PromotionStatus.APPROVED
        and p.startAt <= :now and p.endAt >= :now
      """)
  boolean existsActiveApproved(@Param("productId") Long productId,
      @Param("now") LocalDateTime now);

  @Query("""
      select p.product from ProductPromotion p
      where p.status = geekcode.takatuf.Enums.PromotionStatus.APPROVED
        and p.startAt <= :now and p.endAt >= :now
      order by p.startAt desc
      """)
  Page<Product> findActiveFeaturedProducts(@Param("now") LocalDateTime now,
      Pageable pageable);
}
