package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    List<Promotion> findByStoreId(Long storeId);

    List<Promotion> findByActiveAndStartDateBeforeAndEndDateAfter(Boolean active, LocalDateTime start, LocalDateTime end);
    List<Promotion> findByActive(Boolean active);


}
