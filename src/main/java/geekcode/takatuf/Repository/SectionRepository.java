package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findAllByActiveTrueOrderBySortOrderAsc();

    Page<Section> findAll(Pageable pageable);

    Page<Section> findByNameContainingIgnoreCase(String name, Pageable pageable);

}
