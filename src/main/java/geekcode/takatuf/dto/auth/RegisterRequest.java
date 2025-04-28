package geekcode.takatuf.dto.auth;

import geekcode.takatuf.Enums.RoleName;
import geekcode.takatuf.Enums.UserType;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String name;

    @Email
    private String email;

    private String phoneNumber;

    private String password;

    private UserType type;  // Using Enum for type

    private RoleName role;  // Optional role
}

