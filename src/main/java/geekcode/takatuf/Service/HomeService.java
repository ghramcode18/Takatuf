package geekcode.takatuf.Service;

import geekcode.takatuf.dto.HomeResponse;
import geekcode.takatuf.dto.product.ProductResponse;
import geekcode.takatuf.dto.section.SectionResponse;
import geekcode.takatuf.dto.slider.SliderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class HomeService {

        private final SectionService sectionService;
        private final SliderService sliderService;
        private final PromotionService promotionService;

        public HomeResponse getHomeData() {
                return getHomeData(null);
        }

        public HomeResponse getHomeData(Long viewerId) {
                var sections = sectionService.getAllSections().stream()
                                .filter(s -> Boolean.TRUE.equals(s.getActive()))
                                .sorted(Comparator.comparing(
                                                SectionResponse::getSortOrder,
                                                Comparator.nullsLast(Integer::compareTo)))
                                .toList();

                var sliders = sliderService.getAllSliders();

                var featured = promotionService.getFeaturedProducts(12, viewerId);

                return HomeResponse.builder()
                                .sliders(sliders)
                                .sections(sections)
                                .featuredProducts(featured)
                                .build();
        }
}
