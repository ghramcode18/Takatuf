package geekcode.takatuf.dto.chat;

import java.time.LocalDateTime;

import lombok.*;


@Builder
public record ChatSummaryResponse(
        Long chatId,
        Long otherUserId,
        String otherUsername,
        String otherUserPhotoUrl,
        String lastMessage,
        LocalDateTime lastMessageTime
) {}

