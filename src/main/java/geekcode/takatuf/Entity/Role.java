package geekcode.takatuf.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

import geekcode.takatuf.Enums.RoleName;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private RoleName role;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private List<RolePermission> rolePermissions;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private List<UserRole> userRoles;
}
