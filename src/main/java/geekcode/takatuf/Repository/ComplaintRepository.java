package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {



List<Complaint> findByUserId(Long userId);
List<Complaint> findByStatus(String status);

}
