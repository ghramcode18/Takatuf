package geekcode.takatuf.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class AuthResponse {
    private String message;
    private Long id;
    private String name;
    private String email;
    private String type;
    private String role;
    private String accessToken;
    private String refreshToken;
}