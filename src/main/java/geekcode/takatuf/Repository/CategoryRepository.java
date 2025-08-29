package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  boolean existsByNameIgnoreCase(String name);

  Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);

  @Query("""
          select c from Category c
          where (:q is null or trim(:q) = '' or lower(c.name) like lower(concat('%', :q, '%')))
      """)
  Page<Category> findAllByNameLike(@Param("q") String q, Pageable pageable);

  @Query("""
          select c from Category c
          where (c.active = true or c.active is null)
            and (:q is null or trim(:q) = '' or lower(c.name) like lower(concat('%', :q, '%')))
            and exists (select 1 from Product p where p.category.id = c.id)
      """)
  Page<Category> findCustomerVisible(@Param("q") String q, Pageable pageable);
}
