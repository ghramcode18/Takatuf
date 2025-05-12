package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByStoreId(Long storeId, Pageable pageable);

    Page<Product> findByStoreIdAndNameContainingIgnoreCase(Long storeId, String name, Pageable pageable);

    List<Product> findByCategory(String category);

}
