package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.Chat;
import geekcode.takatuf.Entity.DeletedChat;
import geekcode.takatuf.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeletedChatRepository extends JpaRepository<DeletedChat, Long> {
    Optional<DeletedChat> findByUserAndChat(User user, Chat chat);
    void deleteByUserAndChat(User user, Chat chat);
    boolean existsByUserAndChat(User user, Chat chat);
    void deleteAllByChat(Chat chat);

    boolean existsByChatIdAndUserId(Long chatId, Long userId);




}
