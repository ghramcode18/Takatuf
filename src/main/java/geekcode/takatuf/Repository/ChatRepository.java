package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.Chat;
import geekcode.takatuf.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findByUser1IdOrUser2Id(Long user1Id, Long user2Id);

    Optional<Chat> findByUser1IdAndUser2Id(Long user1Id, Long user2Id);

    @Query("SELECT c FROM Chat c WHERE c.user1.id = :userId OR c.user2.id = :userId")
    List<Chat> findAllByUserId(@Param("userId") Long userId);

    Optional<Chat> findByUser1AndUser2(User user1, User user2);
    Optional<Chat> findByUser2AndUser1(User user1, User user2);

}
