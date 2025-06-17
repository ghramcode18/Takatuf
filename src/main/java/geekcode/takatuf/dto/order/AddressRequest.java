package geekcode.takatuf.dto.order;

import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequest {
    private String firstName;
    private String lastName;
    private String region;
    private String streetName;
    private String buildingNumber;
    private String phoneNumber;
}
