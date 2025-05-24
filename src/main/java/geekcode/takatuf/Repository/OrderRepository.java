package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.Order;
import geekcode.takatuf.Enums.OrderStatus;
import geekcode.takatuf.Enums.OrderType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByStoreId(Long storeId);

    List<Order> findByOrderType(OrderType orderType);

    List<Order> findByUserIdAndOrderType(Long userId, OrderType orderType);

    List<Order> findByStoreIdAndStatus(Long storeId, OrderStatus status);

    List<Order> findByStatusAndCreatedAtBetween(OrderStatus status, LocalDateTime start, LocalDateTime end);
}
