package geekcode.takatuf.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;

@Entity
@Table(name = "profiles")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Setter
@Getter

public class Profile {
    @Id
    private String phone;
    private String address;
    private String paymentInfo;
    private Boolean isActive;
    private Boolean isDeleted;
    private Timestamp createdAt;

    @OneToOne
    @MapsId
    private User user;
}