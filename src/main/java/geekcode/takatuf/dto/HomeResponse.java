package geekcode.takatuf.dto;

import geekcode.takatuf.dto.section.SectionResponse;
import geekcode.takatuf.dto.slider.SliderResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HomeResponse {
    private List<SliderResponse> sliders;
    private List<SectionResponse> sections;
}
