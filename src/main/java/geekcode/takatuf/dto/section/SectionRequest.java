package geekcode.takatuf.dto.section;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import java.util.List;

@Data
public class SectionRequest {
    private String name;
    private String description;
    private String type; // store | product | general
    private Integer sortOrder;
    private Boolean active;
    private MultipartFile image;
    private List<Long> ids;
}