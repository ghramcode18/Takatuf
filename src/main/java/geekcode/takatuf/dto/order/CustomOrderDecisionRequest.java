package geekcode.takatuf.dto.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomOrderDecisionRequest {
    private Boolean accept;
    private BigDecimal proposedPrice;
}
