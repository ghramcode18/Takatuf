package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    List<Order> findByStatus(String status);

    List<Order> findByStoreId_Id(Long storeId);

}
