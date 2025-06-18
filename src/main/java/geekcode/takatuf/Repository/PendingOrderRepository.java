package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.PendingOrder;
import geekcode.takatuf.Enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PendingOrderRepository extends JpaRepository<PendingOrder, Long> {
    Optional<PendingOrder> findByUserIdAndStatus(Long userId, OrderStatus status);
}
