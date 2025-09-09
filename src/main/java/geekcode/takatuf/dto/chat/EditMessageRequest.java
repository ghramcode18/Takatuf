package geekcode.takatuf.dto.chat;

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
