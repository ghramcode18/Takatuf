package geekcode.takatuf.dto.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlaceCustomOrderDecisionRequest {
    private Boolean accept;
    private BigDecimal proposedPrice;
}
