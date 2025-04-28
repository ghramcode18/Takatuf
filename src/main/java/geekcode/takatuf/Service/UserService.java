package geekcode.takatuf.Service;

import geekcode.takatuf.dto.user.UpdateUserRequest;
import geekcode.takatuf.dto.user.UserResponse;
import geekcode.takatuf.Entity.User;
import geekcode.takatuf.Repository.UserRepository;
import geekcode.takatuf.Exception.Types.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }

        if (updateRequest.getType() != null) {
            existingUser.setType(updateRequest.getType());
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
