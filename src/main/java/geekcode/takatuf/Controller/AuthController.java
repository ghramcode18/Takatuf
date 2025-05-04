package geekcode.takatuf.Controller;

import geekcode.takatuf.Service.AuthService;
import geekcode.takatuf.dto.auth.AuthResponse;
import geekcode.takatuf.dto.auth.EmailRequest;
import geekcode.takatuf.dto.auth.LoginRequest;
import geekcode.takatuf.dto.auth.OtpVerificationRequest;
import geekcode.takatuf.dto.auth.RefreshTokenRequest;
import geekcode.takatuf.dto.auth.RegisterRequest;
import geekcode.takatuf.dto.auth.ResetPasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody LoginRequest request) {
        AuthResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok("Logout successfully");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody EmailRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("OTP has been sent to your email.");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequest request) {
        authService.verifyOtp(request.getOtp());
        return ResponseEntity.ok("OTP verified");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getNewPassword());
        return ResponseEntity.ok("Password reset successfully");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

}