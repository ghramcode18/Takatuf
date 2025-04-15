package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByDescription(String description);

}
