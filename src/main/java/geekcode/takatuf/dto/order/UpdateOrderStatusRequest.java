package geekcode.takatuf.dto.order;

import geekcode.takatuf.Enums.OrderStatus;
import geekcode.takatuf.Enums.TrackingInfo;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    private OrderStatus status;
    private TrackingInfo trackingInfo;
}
