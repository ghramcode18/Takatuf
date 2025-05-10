package geekcode.takatuf.Entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data

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
