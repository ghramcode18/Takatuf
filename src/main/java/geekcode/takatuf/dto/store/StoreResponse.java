package geekcode.takatuf.dto.store;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StoreResponse {
    private Long id;
    private String name;
    private String description;
    private String status;
    private String imageUrl;

    private String ownerName;
    private String ownerEmail;

    private Double averageRating;
    private Integer totalReviews;
    private long totalProducts;
}
