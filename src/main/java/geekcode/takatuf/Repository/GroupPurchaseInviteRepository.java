package geekcode.takatuf.Repository;


import geekcode.takatuf.Entity.GroupPurchaseInvite;
import geekcode.takatuf.Entity.User;
import geekcode.takatuf.Enums.InviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface GroupPurchaseInviteRepository extends JpaRepository<GroupPurchaseInvite, Long> {

    List<GroupPurchaseInvite> findByStatusAndExpiresAtBefore(InviteStatus status, LocalDateTime dateTime);

    List<GroupPurchaseInvite> findBySenderIdOrderByCreatedAtDesc(Long senderId);

    User  findBySenderId(Long senderId);
    List<GroupPurchaseInvite> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);

}
