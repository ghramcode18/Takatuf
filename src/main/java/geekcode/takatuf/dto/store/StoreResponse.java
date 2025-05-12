package geekcode.takatuf.dto.store;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreResponse {
    private Long id;
    private String name;
    private String description;
    private String status;
    private String ownerEmail;
    private String ownerName;
}
