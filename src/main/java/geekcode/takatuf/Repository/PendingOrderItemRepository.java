package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.PendingOrder;
import geekcode.takatuf.Entity.PendingOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PendingOrderItemRepository extends JpaRepository<PendingOrderItem, Long> {
    List<PendingOrderItem> findByPendingOrder(PendingOrder pendingOrder);
    void deleteAllByPendingOrder(PendingOrder pendingOrder);

}
