package geekcode.takatuf.dto.user;


import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String type;
    private List<String> roles;
}
