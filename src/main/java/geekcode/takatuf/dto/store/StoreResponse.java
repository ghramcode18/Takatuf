package geekcode.takatuf.dto.store;

<<<<<<< HEAD
import org.springframework.web.multipart.MultipartFile;
=======

>>>>>>> b2a8628fee48e2cf35c8a0849a1c200336ddb60c

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
}
