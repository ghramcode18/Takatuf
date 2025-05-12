package geekcode.takatuf.Controller;

import geekcode.takatuf.dto.user.UpdateUserRequest;
import geekcode.takatuf.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import geekcode.takatuf.dto.user.UserResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/update")
    public ResponseEntity<UserResponse> updateUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateUserRequest updateRequest) {

        String email = userDetails.getUsername();
        Long userId = userService.findUserIdByEmail(email);

        UserResponse response = userService.updateUser(userId, updateRequest);
        return ResponseEntity.ok(response);
    }
}