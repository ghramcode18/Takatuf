package geekcode.takatuf.Service;

import geekcode.takatuf.dto.HomeResponse;
import geekcode.takatuf.dto.section.SectionResponse;
import geekcode.takatuf.dto.slider.SliderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class HomeService {

    private final SectionService sectionService;
    private final SliderService sliderService;

    public HomeResponse getHomeData() {
        List<SectionResponse> sections = sectionService.getAllSections().stream()
                .filter(s -> Boolean.TRUE.equals(s.getActive()))
                .sorted(Comparator.comparing(
                        SectionResponse::getSortOrder,
                        Comparator.nullsLast(Integer::compareTo)))
                .toList();

        List<SliderResponse> sliders = sliderService.getAllSliders();

        return HomeResponse.builder()
                .sliders(sliders)
                .sections(sections) 
                .build();
    }
}
