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
        List<SectionResponse> sections = sectionService.getAllSections();

        Map<String, List<SectionResponse>> groupedSections = sections.stream()
                .collect(Collectors.groupingBy(section -> section.getType().toUpperCase()));

        List<Map<String, Object>> resultSections = new ArrayList<>();

        groupedSections.forEach((type, sectionList) -> {
            List<Object> allData = sectionList.stream()
                    .flatMap(s -> s.getData().stream())
                    .collect(Collectors.toList());

            Map<String, Object> sectionObject = new HashMap<>();
            sectionObject.put("type", type);
            sectionObject.put("data", allData);

            resultSections.add(sectionObject);
        });

        List<SliderResponse> sliders = sliderService.getAllSliders();

        return HomeResponse.builder()
                .sliders(sliders)
                .sections(resultSections)
                .build();
    }
}