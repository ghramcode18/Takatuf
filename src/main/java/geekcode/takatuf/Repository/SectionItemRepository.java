package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.SectionItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionItemRepository extends JpaRepository<SectionItem, Long> {
    void deleteBySectionId(Long sectionId);
}
