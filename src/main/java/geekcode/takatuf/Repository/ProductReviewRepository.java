package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    List<ProductReview> findByProduct_Id(Long productId);
}
