package geekcode.takatuf.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import geekcode.takatuf.Entity.Section;
import geekcode.takatuf.Entity.Slider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SliderRepository extends JpaRepository<Slider, Long> {

    Page<Slider> findAll(Pageable pageable);
}