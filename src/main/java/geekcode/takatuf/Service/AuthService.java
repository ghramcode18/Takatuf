package geekcode.takatuf.Service;

import geekcode.takatuf.Entity.User;
import geekcode.takatuf.Entity.UserRole;
import geekcode.takatuf.Entity.OTPInfo;
import geekcode.takatuf.Entity.Role;
import geekcode.takatuf.Repository.UserRepository;
import geekcode.takatuf.Repository.RoleRepository;
import geekcode.takatuf.Repository.UserRoleRepository;
import geekcode.takatuf.Exception.Types.BadRequestException;
import geekcode.takatuf.Security.JwtService;
import geekcode.takatuf.dto.auth.AuthResponse;
import geekcode.takatuf.dto.auth.LoginRequest;
import geekcode.takatuf.dto.auth.RegisterRequest;

import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import java.util.Map;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JavaMailSender mailSender;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        if (userRepository.existsByName(request.getName())) {
            throw new BadRequestException("User name already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .type(request.getType())
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        if (request.getRole() != null) {
            Role role = roleRepository.findByRoleName(request.getRole())
                    .orElseThrow(() -> new BadRequestException("Invalid role"));

            UserRole userRole = UserRole.builder()
                    .user(user)
                    .role(role)
                    .build();

            userRoleRepository.save(userRole);
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .type(user.getType().name())
                .role(request.getRole() != null ? request.getRole().name() : null)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .message("Registered Successfully")
                .build();
    }

    public AuthResponse authenticate(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseGet(() -> userRepository.findByName(request.getEmail())
                        .orElseThrow(() -> new BadRequestException("Invalid credentials")));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .type(user.getType().name())
                .role(user.getUserRoles().isEmpty() ? null : user.getUserRoles().get(0).getRole().getRoleName().name())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .message("Login Successfully")
                .build();
    }

    public void logout(String token) {
        System.out.println("Logging out the user. Token invalidated: " + token);
    }

    private final Map<String, OTPInfo> otpStorage = new HashMap<>();
    private final Map<String, String> otpToEmailMap = new HashMap<>();
    private String lastVerifiedEmail;
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Email not found"));

        String otp = generateOtp();
        otpStorage.put(email, new OTPInfo(otp, LocalDateTime.now(), false));
        otpToEmailMap.put(otp, email);

        sendOtpEmail(email, otp);
    }

    public void verifyOtp(String otp) {
        String email = otpToEmailMap.get(otp);

        if (email == null) {
            throw new BadRequestException("Invalid OTP");
        }

        OTPInfo otpInfo = otpStorage.get(email);

        if (otpInfo == null || !otpInfo.getOtp().equals(otp)) {
            throw new BadRequestException("Invalid OTP");
        }

        if (otpInfo.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now())) {
            otpStorage.remove(email);
            otpToEmailMap.remove(otp);
            throw new BadRequestException("OTP expired");
        }

        otpInfo.setVerified(true);
        lastVerifiedEmail = email;
    }

    public void resetPassword(String newPassword) {
        if (lastVerifiedEmail == null) {
            throw new BadRequestException("OTP verification required");
        }
    
        OTPInfo otpInfo = otpStorage.get(lastVerifiedEmail);
    
        if (otpInfo == null || !otpInfo.isVerified()) {
            throw new BadRequestException("OTP verification required");
        }
    
        User user = userRepository.findByEmail(lastVerifiedEmail)
                .orElseThrow(() -> new BadRequestException("Email not found"));
    
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    
        otpStorage.remove(lastVerifiedEmail);
        otpToEmailMap.values().removeIf(e -> e.equals(lastVerifiedEmail));
        lastVerifiedEmail = null;
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset OTP");
        message.setText("Your OTP code is: " + otp);
        mailSender.send(message);
    }

    public AuthResponse refreshToken(String refreshToken) {
        String email = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        if (!jwtService.isTokenValid(refreshToken)) {
            throw new BadRequestException("Invalid refresh token");
        }

        String newAccessToken = jwtService.generateAccessToken(user);

        return AuthResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .type(user.getType().name())
                .role(user.getUserRoles().isEmpty() ? null : user.getUserRoles().get(0).getRole().getRoleName().name())
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .message("Access token refreshed successfully")
                .build();
    }

}
