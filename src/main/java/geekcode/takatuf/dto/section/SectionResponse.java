package geekcode.takatuf.dto.section;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SectionResponse {
    private Long id;
    private String name;
    private String description;
    private String type;
    private String imageUrl;
    private Integer sortOrder;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Long> ids;
    private List<?> data;

}
