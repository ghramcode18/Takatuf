package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.Chat;
import geekcode.takatuf.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;


@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySenderId(Long senderId);

    List<Message> findByReceiverId(Long receiverId);

    List<Message> findByChatIdAndTimestampBetweenOrderByTimestampDesc(
            Long chatId, LocalDateTime start, LocalDateTime end);
    List<Message> findByChatIdAndTimestampBetweenAndDeletedFalseOrderByTimestampDesc
            (Long chatId, LocalDateTime start, LocalDateTime end);

    List<Message> findByChatIdAndTimestampAfterOrderByTimestampAsc(Long chatId, LocalDateTime from);
    List<Message>  findByChatIdAndTimestampBetweenAndDeletedFalseOrderByTimestampAsc(Long chatId, LocalDateTime start, LocalDateTime end);

    Optional<Message> findTopByChatOrderByTimestampDesc(Chat chat);

    List<Message> findByChatIdAndTimestampBetweenAndDeletedFalse(Long chatId, LocalDateTime start, LocalDateTime end);
}
