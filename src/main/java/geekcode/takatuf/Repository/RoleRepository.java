package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.Role;
import geekcode.takatuf.Enums.RoleName;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRole(RoleName role);

}
