package geekcode.takatuf.dto.auth;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTPInfo {
    private final String otp;
    private final LocalDateTime createdAt;
    private boolean verified;

    public OTPInfo(String otp, LocalDateTime createdAt, boolean verified) {
        this.otp = otp;
        this.createdAt = createdAt;
        this.verified = verified;
    }
}
