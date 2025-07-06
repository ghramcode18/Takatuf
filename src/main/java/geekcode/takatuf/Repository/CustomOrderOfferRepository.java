package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.CustomOrderOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

public interface CustomOrderOfferRepository extends JpaRepository<CustomOrderOffer, Long> {
    List<CustomOrderOffer> findByOrderId(Long orderId);
}