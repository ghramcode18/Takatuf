package geekcode.takatuf.dto.section;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class SectionRequest {
    private String name;
    private String description;
    private String type; // store | product | general
    private Integer sortOrder;
    private Boolean active;
    private MultipartFile image;
}
