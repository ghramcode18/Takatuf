package geekcode.takatuf.dto.order;

import geekcode.takatuf.Enums.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OfferDto {
    

@Data
public static class SubmitOfferRequest {
    private BigDecimal proposedPrice;
    private String additionalInfo;
}

@Data
public static class OfferResponse {
    private Long offerId;
    private Long sellerId;
    private String sellerName;
    private BigDecimal proposedPrice;
    private String additionalInfo;
    private OfferStatus status;
}

@Data
public static class BuyerOfferDecisionRequest {
    private boolean accept;
    private Long sellerId;
}

}
