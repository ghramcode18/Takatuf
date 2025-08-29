package geekcode.takatuf.dto.section;

import lombok.Data;

@Data
public class SectionItemSortRequest {
    private Long itemId;
    private Integer sortOrder;
}