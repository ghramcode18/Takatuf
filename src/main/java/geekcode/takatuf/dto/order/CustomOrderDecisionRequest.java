package geekcode.takatuf.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomOrderDecisionRequest {
    private Boolean accept;
    private BigDecimal proposedPrice;
}
