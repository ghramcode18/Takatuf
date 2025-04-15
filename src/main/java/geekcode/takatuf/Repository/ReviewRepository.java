package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProduct_Id(Long productId);

    List<Review> findByUser_Id(Long userId);

}
