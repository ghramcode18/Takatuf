package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findAllByActiveTrueOrderBySortOrderAsc();
}
