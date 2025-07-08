package geekcode.takatuf.dto.slider;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class SliderRequest {
    private String title;
    private String description;
    private MultipartFile image;
    private String targetUrl;
    private String type;
    private boolean active;
    private Integer priority;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
