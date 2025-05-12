package geekcode.takatuf.dto.order;

import lombok.*;

@Data
public class PlaceCustomOrderRequest {
    private Long storeId;
    private String category;
    private String customizationDetails;
}
