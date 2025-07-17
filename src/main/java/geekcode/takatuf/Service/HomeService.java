package geekcode.takatuf.Service;

import geekcode.takatuf.dto.HomeResponse;
import geekcode.takatuf.dto.section.SectionResponse;
import geekcode.takatuf.dto.slider.SliderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final SliderService sliderService;
    private final SectionService sectionService;

    public HomeResponse getHomeData() {
        List<SliderResponse> sliders = sliderService.getAllSliders();
        List<SectionResponse> sections = sectionService.getAllSections();

        return HomeResponse.builder()
                .sliders(sliders)
                .sections(sections)
                .build();
    }
}
