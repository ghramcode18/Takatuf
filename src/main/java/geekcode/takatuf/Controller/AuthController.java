package geekcode.takatuf.Controller;

import geekcode.takatuf.Entity.RefreshTokenRequest;
import geekcode.takatuf.Security.JwtService;
import geekcode.takatuf.Service.AuthService;
import geekcode.takatuf.dto.MessageResponse;
import geekcode.takatuf.dto.auth.AuthResponse;
import geekcode.takatuf.dto.auth.EmailRequest;
import geekcode.takatuf.dto.auth.LoginRequest;
import geekcode.takatuf.dto.auth.OtpVerificationRequest;
import geekcode.takatuf.dto.auth.RegisterRequest;
import geekcode.takatuf.dto.auth.ResetPasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    JwtService jwtUtil;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(new MessageResponse("The User Register Successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody LoginRequest request) {
        AuthResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String email = jwtUtil.extractEmail(token);
        authService.logout(email);
        return ResponseEntity.ok(new MessageResponse("Logout successfully"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@RequestBody EmailRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(new MessageResponse("OTP has been sent to your email."));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<MessageResponse> verifyOtp(@RequestBody OtpVerificationRequest request) {
        authService.verifyOtp(request.getOtp());
        return ResponseEntity.ok(new MessageResponse("OTP verified"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getNewPassword());
        return ResponseEntity.ok(new MessageResponse("Password reset successfully"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

}