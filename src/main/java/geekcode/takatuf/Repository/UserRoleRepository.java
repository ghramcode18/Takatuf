package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findByUserId(Long userId);

    List<UserRole> findByRoleId(Long roleId);

}
