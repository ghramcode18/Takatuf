package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.CustomOrderOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomOrderOfferRepository extends JpaRepository<CustomOrderOffer, Long> {

    List<CustomOrderOffer> findByOrderId(Long orderId);

    List<CustomOrderOffer> findBySellerId(Long sellerId);

    boolean existsByOrderIdAndSellerEmail(Long orderId, String sellerEmail);

    boolean existsByOrderIdAndSellerId(Long orderId, Long sellerId);

}
