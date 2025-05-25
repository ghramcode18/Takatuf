package geekcode.takatuf.Controller;

import geekcode.takatuf.dto.user.UpdateUserRequest;
import geekcode.takatuf.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import geekcode.takatuf.dto.user.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import geekcode.takatuf.Enums.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> updateUser(
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
        UserResponse response = userService.updateUser(userId, updateRequest);

        return ResponseEntity.ok(response);
    }

}