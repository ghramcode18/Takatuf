package geekcode.takatuf.dto.slider;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SliderRequest {
    private String title;
    private String description;
    private String imageUrl;
    private String targetUrl;
    private String type;
    private boolean active;
    private Integer priority;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
