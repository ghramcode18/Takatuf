package geekcode.takatuf.dto.group_purchase;


import geekcode.takatuf.Enums.InviteStatus;
import java.time.LocalDateTime;
import lombok.*;


@Builder
public record GroupPurchaseInviteResponse(
        Long inviteId,
        Long otherUserId,
        String otherUserName,
        String otherUserPhoto,
        String message,
        InviteStatus status,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        LocalDateTime respondedAt
) {}
