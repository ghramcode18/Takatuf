package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;


@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    List<Promotion> findByStoreId(Long storeId);

}
