package geekcode.takatuf.Service;

import geekcode.takatuf.dto.user.UpdateUserRequest;
import geekcode.takatuf.dto.user.UserResponse;
import geekcode.takatuf.Entity.User;
import geekcode.takatuf.Repository.UserRepository;
import geekcode.takatuf.Exception.Types.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;
import java.nio.file.Path;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String storeProfileImage(MultipartFile imageFile) throws IOException {
        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        Path path = Paths.get("uploads/user/images/" + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, imageFile.getBytes());
        return "/uploads/user/images/" + fileName;
    }

    public UserResponse updateUser(Long userId, UpdateUserRequest updateRequest) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(updateRequest.getEmail())) {
                throw new BadRequestException("Email already in use");
            }
            existingUser.setEmail(updateRequest.getEmail());
        }

        if (updateRequest.getName() != null) {
            existingUser.setName(updateRequest.getName());
        }

        if (updateRequest.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(updateRequest.getPhoneNumber());
        }

        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }

        if (updateRequest.getProfileImageUrl() != null) {
            existingUser.setProfileImageUrl(updateRequest.getProfileImageUrl());
        }

        existingUser.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(existingUser);

        return convertToUserResponse(updatedUser);
    }

    private UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .type(user.getType().name())
                .profileImageUrl(user.getProfileImageUrl())
                .roles(
                        user.getUserRoles().stream()
                                .map(userRole -> userRole.getRole().getRoleName().name())
                                .collect(Collectors.toList()))
                .build();
    }

    public Long findUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"))
                .getId();
    }
}
