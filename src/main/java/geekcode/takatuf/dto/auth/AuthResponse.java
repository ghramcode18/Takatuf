package geekcode.takatuf.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
}