package geekcode.takatuf.Controller;

import geekcode.takatuf.Entity.User;
import geekcode.takatuf.Exception.Types.ResourceNotFoundException;
import geekcode.takatuf.Repository.UserRepository;
import geekcode.takatuf.dto.MessageResponse;
import geekcode.takatuf.dto.user.UpdateUserRequest;
import geekcode.takatuf.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import geekcode.takatuf.dto.user.UserResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> updateUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setName(name);
        updateRequest.setEmail(email);
        updateRequest.setPhoneNumber(phoneNumber);
        updateRequest.setPassword(password);

        if (image != null && !image.isEmpty()) {
            String imageUrl = userService.storeProfileImage(image);
            updateRequest.setProfileImageUrl(imageUrl);
        }

        Long userId = userService.findUserIdByEmail(userDetails.getUsername());
        userService.updateUser(userId, updateRequest);

        return ResponseEntity.ok(new MessageResponse("User updated successfully."));
    }

    @GetMapping("/getUserInfo")
    public ResponseEntity<User> getUserInfo(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long id) {
        User user = userService.findUserId(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        Long userId = userService.findUserIdByEmail(userDetails.getUsername());
        UserResponse user = userService.getUserById(userId);

        return ResponseEntity.ok(user);
    }


    @GetMapping("/invitable")
    public List<User> getInvitableUsers(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return userService.findAllExcept(user.getId());
    }
}