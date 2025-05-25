package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.StoreReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreReviewRepository extends JpaRepository<StoreReview, Long> {

    List<StoreReview> findByStore_Id(Long storeId);

    Optional<StoreReview> findByReviewer_IdAndStore_Id(Long reviewerId, Long storeId);
}
