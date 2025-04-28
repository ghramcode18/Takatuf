package geekcode.takatuf.Service;

import geekcode.takatuf.Entity.*;
import geekcode.takatuf.Repository.ProfileRepository;
import geekcode.takatuf.Repository.UserRepository;
import geekcode.takatuf.Repository.RoleRepository;
import geekcode.takatuf.Repository.UserRoleRepository;
import geekcode.takatuf.Exception.Types.BadRequestException;
import geekcode.takatuf.Security.JwtService;
import geekcode.takatuf.dto.auth.AuthResponse;
import geekcode.takatuf.dto.auth.LoginRequest;
import geekcode.takatuf.dto.auth.RegisterRequest;

import org.springframework.transaction.annotation.Transactional;

import java.security.PrivateKey;
import java.util.*;

import lombok.RequiredArgsConstructor;

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

    private final ProfileRepository profileRepository;

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        if (userRepository.existsByName(request.getName())) {
            throw new BadRequestException("User name already registered");
        }

        Profile profile = Profile.builder()
                .isActive(true)
                .isDeleted(false)
                .phone(request.getPhoneNumber())
                .createdAt(LocalDateTime.now())
                .build();

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .type(request.getType())
                .profile(profile)
                .createdAt(LocalDateTime.now())
                .build();

        profile.setUser(user);
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

        // إنشاء التوكنات
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
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
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void logout(String email) {

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new BadRequestException("User not found");
        }
        User user = userOptional.get();
        Profile profile = user.getProfile();
        if (profile != null) {
            profile.setIsActive(false);
        }
        userRepository.save(user);
    }

    private final Map<String, OTPInfo> otpStorage = new HashMap<>();

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Email not found"));

        String otp = generateOtp();
        otpStorage.put(email, new OTPInfo(otp, LocalDateTime.now(), false));

        sendOtpEmail(email, otp);
    }

    public void verifyOtp(String email, String otp) {
        OTPInfo otpInfo = otpStorage.get(email);

        if (otpInfo == null || !otpInfo.getOtp().equals(otp)) {
            throw new BadRequestException("Invalid OTP");
        }

        if (otpInfo.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now())) {
            otpStorage.remove(email);
            throw new BadRequestException("OTP expired");
        }

        otpInfo.setVerified(true);
    }

    public void resetPassword(String email, String newPassword) {
        OTPInfo otpInfo = otpStorage.get(email);

        if (otpInfo == null || !otpInfo.isVerified()) {
            throw new BadRequestException("OTP verification required");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Email not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpStorage.remove(email);
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
                .accessToken(newAccessToken)
                .build();
    }

}
