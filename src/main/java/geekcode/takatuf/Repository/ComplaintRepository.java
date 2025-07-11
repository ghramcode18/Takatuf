package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import geekcode.takatuf.Enums.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findBySubmittedBy_Id(Long userId);

    List<Complaint> findByStatus(String status);

    Page<Complaint> findBySubmittedBy_Type(UserType userType, Pageable pageable);


}
