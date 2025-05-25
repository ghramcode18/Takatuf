package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.SellerReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerReviewRepository extends JpaRepository<SellerReview, Long> {
    List<SellerReview> findBySeller_Id(Long sellerId);

    Optional<SellerReview> findByReviewer_IdAndSeller_Id(Long reviewerId, Long sellerId);

}
