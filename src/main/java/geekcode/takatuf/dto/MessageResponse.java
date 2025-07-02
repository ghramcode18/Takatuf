package geekcode.takatuf.dto;

<<<<<<< HEAD
import lombok.*;

@Data
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private String message;

=======
public class MessageResponse {
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
>>>>>>> b2a8628fee48e2cf35c8a0849a1c200336ddb60c
}
