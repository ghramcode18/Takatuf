package geekcode.takatuf.dto.slider;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
public class SliderRequest {
    private String title;
    private String description;
    private MultipartFile image;

    private String type; // "STORE", "PRODUCT", "LINK", "NONE"
    private String linkUrl;
    private Long targetId;

    private Integer priority;
    private Boolean active;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
