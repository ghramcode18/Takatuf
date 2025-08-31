package geekcode.takatuf.dto;

import geekcode.takatuf.dto.slider.SliderResponse;
import lombok.Builder;
import lombok.Data;
import geekcode.takatuf.dto.product.ProductResponse;
import geekcode.takatuf.dto.section.SectionResponse;
import geekcode.takatuf.dto.slider.SliderResponse;
import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class HomeResponse {
    private List<SliderResponse> sliders;
    private List<SectionResponse> sections;
    private List<ProductResponse> featuredProducts;
}
