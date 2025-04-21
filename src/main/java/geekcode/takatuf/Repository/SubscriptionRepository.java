package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByUser_Id(Long userId);

    List<Subscription> findByStatus(String status);

}
