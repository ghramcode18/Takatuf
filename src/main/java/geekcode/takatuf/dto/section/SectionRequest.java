package geekcode.takatuf.dto.section;

import lombok.Data;

@Data
public class SectionRequest {
    private String name;
    private String description;
    private String type; // store | product | general
    private Integer sortOrder;
    private Boolean active;
    private String image;
}
