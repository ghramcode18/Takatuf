package geekcode.takatuf.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    private String otp;
    private String newPassword;
}
