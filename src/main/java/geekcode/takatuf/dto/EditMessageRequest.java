package geekcode.takatuf.dto;

import lombok.*;


@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EditMessageRequest {
    private String content;
}
