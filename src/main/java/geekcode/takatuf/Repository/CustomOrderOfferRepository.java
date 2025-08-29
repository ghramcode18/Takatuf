package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.CustomOrderOffer;
import geekcode.takatuf.Enums.OfferStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;

@Repository
public interface CustomOrderOfferRepository extends JpaRepository<CustomOrderOffer, Long> {

    List<CustomOrderOffer> findByOrderId(Long orderId);

    List<CustomOrderOffer> findBySellerId(Long sellerId);

    boolean existsByOrderIdAndSellerEmail(Long orderId, String sellerEmail);

    boolean existsByOrderIdAndSellerId(Long orderId, Long sellerId);

    boolean existsByOrderIdAndSellerIdAndStatus(Long orderId, Long sellerId, OfferStatus status);

    Optional<CustomOrderOffer> findFirstByOrderIdAndSellerIdAndStatus(Long orderId, Long sellerId, OfferStatus status);

    boolean existsByOrderIdAndSellerIdAndStatusNot(Long orderId, Long sellerId, OfferStatus status);

    Optional<CustomOrderOffer> findFirstByOrderIdAndSellerIdAndStatusNot(Long orderId, Long sellerId,
            OfferStatus status);

    Optional<CustomOrderOffer> findTopByOrderIdAndStatusOrderByUpdatedAtDesc(Long orderId, OfferStatus status);

    Optional<CustomOrderOffer> findFirstByOrderIdAndStatusOrderByUpdatedAtDesc(Long orderId, OfferStatus status);
}