package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
