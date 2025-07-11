package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.GroupPurchaseOrder;
import geekcode.takatuf.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupPurchaseOrderRepository extends JpaRepository<GroupPurchaseOrder, Long> {

    User findByCreatorId(Long creatorId);
}
