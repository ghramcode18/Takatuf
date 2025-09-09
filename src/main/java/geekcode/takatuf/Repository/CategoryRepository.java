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

    // للبائع: كل الفئات (مع البحث الاختياري)
    @Query("""
        select c from Category c
        where (coalesce(:q, '') = '' or lower(c.name) like concat('%', lower(:q), '%'))
    """)
    Page<Category> findAllByNameLike(@Param("q") String q, Pageable pageable);

    // للمشتري: الفئات المفعلة (أو null نعتبرها مفعلة)
    // + لازم يكون في منتج واحد على الأقل مربوط بالفئة
    @Query("""
        select c from Category c
        where (c.active = true or c.active is null)
          and (coalesce(:q, '') = '' or lower(c.name) like concat('%', lower(:q), '%'))
          and exists (
              select 1 from Product p
              where p.category.id = c.id
                and coalesce(p.quantity, 0) > 0
          )
    """)
    Page<Category> findCustomerVisible(@Param("q") String q, Pageable pageable);
}
