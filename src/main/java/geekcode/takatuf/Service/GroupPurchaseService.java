package geekcode.takatuf.Service;

import geekcode.takatuf.Entity.GroupPurchaseInvite;
import geekcode.takatuf.Entity.GroupPurchaseOrder;
import geekcode.takatuf.Enums.GroupPurchaseStatus;
import geekcode.takatuf.Enums.InviteStatus;
import geekcode.takatuf.Exception.Types.UnauthorizedException;
import geekcode.takatuf.Repository.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import geekcode.takatuf.Entity.User;
import geekcode.takatuf.Exception.Types.ResourceNotFoundException;
import geekcode.takatuf.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class GroupPurchaseService {

    private final UserRepository userRepository;
    private final GroupPurchaseInviteRepository groupPurchaseInviteRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private final  GroupPurchaseOrderRepository groupPurchaseOrderRepository;

    public GroupPurchaseInvite sendInvite(Long senderId, Long receiverId, String message) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        GroupPurchaseInvite invite = GroupPurchaseInvite.builder()
                .sender(sender)
                .receiver(receiver)
                .message(message)
                .status(InviteStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();

        GroupPurchaseInvite saved = groupPurchaseInviteRepository.save(invite);


        messagingTemplate.convertAndSendToUser(
                String.valueOf(receiver.getId()),
                "/queue/notifications",
                new ChatMessageNotification("group_purchase_invite", saved.getId())
        );

        return saved;
    }

        private final GroupPurchaseInviteRepository inviteRepository;

        @Scheduled(cron = "0 0 * * * *") // check every hour
        public void expireOldInvites() {
            List<GroupPurchaseInvite> invites = inviteRepository.findByStatusAndExpiresAtBefore(
                    InviteStatus.PENDING, LocalDateTime.now());

            for (GroupPurchaseInvite invite : invites) {
                invite.setStatus(InviteStatus.EXPIRED);
            }

            inviteRepository.saveAll(invites);

            System.out.println("⏳ تم تحديث الدعوات المنتهية تلقائيًا.");
        }

    @Transactional
    public void acceptGroupPurchaseInvitation(Long invitationId, Long userId) {

        GroupPurchaseInvite invitation = inviteRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

        if (!invitation.getReceiver().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to accept this invitation");
        }

        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Invitation has expired");
        }

        if (invitation.getStatus() != InviteStatus.PENDING) {
            throw new IllegalStateException("Invitation already responded to");
        }

        // تحديث الدعوة
        invitation.setStatus(InviteStatus.ACCEPTED);
        invitation.setRespondedAt(LocalDateTime.now());
        inviteRepository.save(invitation);

        // ‑‑ إذا كان الـ creator هو المرسل نفسه ‑‑
        User creator = invitation.getSender();

        GroupPurchaseOrder order = GroupPurchaseOrder.builder()
                .invitation(invitation)
                .creator(creator)
                .productId(invitation.getGroupId())
                .participants(List.of(creator, invitation.getReceiver()))
                .createdAt(LocalDateTime.now())
                .status(GroupPurchaseStatus.IN_PROGRESS)
                .build();

        groupPurchaseOrderRepository.save(order);

        // إشعار للمرسل
        messagingTemplate.convertAndSendToUser(
                String.valueOf(creator.getId()),
                "/queue/notifications",
                new ChatMessageNotification("group_invitation_accepted", invitation.getId())
        );
    }


    @Transactional
    public void rejectGroupPurchaseInvitation(Long invitationId, Long userId) {
        GroupPurchaseInvite invitation = inviteRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

        if (!invitation.getReceiver().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to reject this invitation");
        }

        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Invitation has expired");
        }

        if (!invitation.getStatus().equals(InviteStatus.PENDING)) {
            throw new IllegalStateException("Invitation already responded to");
        }

        invitation.setStatus(InviteStatus.REJECTED);
        invitation.setRespondedAt(LocalDateTime.now());
        inviteRepository.save(invitation);

        Long senderId = invitation.getSender().getId();
        messagingTemplate.convertAndSendToUser(
                String.valueOf(senderId),
                "/queue/notifications",
                new ChatMessageNotification("group_invitation_rejected", invitation.getId())
        );
    }
    @Transactional
    public void cancelGroupPurchaseOrder(Long orderId, Long userId) {
        GroupPurchaseOrder order = groupPurchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getCreator().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to cancel this order");
        }

        if (order.getStatus() == GroupPurchaseStatus.CANCELLED) {
            throw new IllegalStateException("Order already cancelled");
        }

        order.setStatus(GroupPurchaseStatus.CANCELLED);
        groupPurchaseOrderRepository.save(order);

        for (User participant : order.getParticipants()) {
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(participant.getId()),
                    "/queue/notifications",
                    new ChatMessageNotification("group_order_cancelled", order.getId())
            );
        }
    }

    @Transactional(readOnly = true)
    public List<GroupPurchaseInviteResponse> getSentInvites(Long userId) {
        return inviteRepository.findBySenderIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GroupPurchaseInviteResponse> getReceivedInvites(Long userId) {
        return inviteRepository.findByReceiverIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }


    private GroupPurchaseInviteResponse mapToDto(GroupPurchaseInvite invite) {
        boolean currentIsSender = invite.getSender().getId().equals(invite.getReceiver().getId());
        var other = currentIsSender ? invite.getReceiver() : invite.getSender();

        return new GroupPurchaseInviteResponse(
                invite.getId(),
                other.getId(),
                other.getName(),
                other.getProfileImageUrl(),
                invite.getMessage(),
                invite.getStatus(),
                invite.getCreatedAt(),
                invite.getExpiresAt(),
                invite.getRespondedAt()
        );
    }

}
