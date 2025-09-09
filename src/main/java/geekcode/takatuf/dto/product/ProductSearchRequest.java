package geekcode.takatuf.dto.product;

import lombok.Data;
import java.util.List;

@Data
public class ProductSearchRequest {
    private String search;
    private List<Long> ids;
}