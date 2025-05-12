package geekcode.takatuf.dto.store;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreRequest {
    private String name;
    private String description;
    private String status;
}
