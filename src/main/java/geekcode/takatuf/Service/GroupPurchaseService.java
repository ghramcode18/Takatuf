package geekcode.takatuf.Service;

import geekcode.takatuf.Entity.GroupPurchaseInvite;
import geekcode.takatuf.Entity.GroupPurchaseOrder;
import geekcode.takatuf.Entity.Product;
import geekcode.takatuf.Enums.GroupPurchaseStatus;
import geekcode.takatuf.Enums.InviteStatus;
import geekcode.takatuf.Exception.Types.UnauthorizedException;
import geekcode.takatuf.Repository.*;
import geekcode.takatuf.dto.group_purchase.GroupPurchaseInviteDetailsResponse;
import geekcode.takatuf.dto.group_purchase.SendInviteRequest;
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

    private final ProductRepository productRepository;
    private final  GroupPurchaseOrderRepository groupPurchaseOrderRepository;

    public GroupPurchaseInvite sendInvite(SendInviteRequest request) {
        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        GroupPurchaseInvite invite = GroupPurchaseInvite.builder()
                .sender(sender)
                .receiver(receiver)
                .message(request.getMessage())
                .productId(request.getProductId()) // 👈 جديد
                .status(InviteStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();

        GroupPurchaseInvite saved = groupPurchaseInviteRepository.save(invite);

        // إشعار للمستلم
        messagingTemplate.convertAndSendToUser(
                String.valueOf(receiver.getId()),
                "/queue/notifications",
                new ChatMessageNotification("group_purchase_invite", saved.getId())
        );

        return saved;
    }
    public GroupPurchaseInviteDetailsResponse getInviteDetails(Long inviteId) {
        GroupPurchaseInvite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new ResourceNotFoundException("Invite not found"));

        Product product = productRepository.findById(invite.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        User sender = invite.getSender();
        User receiver = invite.getReceiver();

        double oldPrice = product.getPrice().doubleValue();
        double discount = product.getGroupDiscountPercentage().doubleValue();
        double newPrice = oldPrice - (oldPrice * discount / 100);

        return new GroupPurchaseInviteDetailsResponse(
                invite.getId(),
                product.getId(),
                product.getName(),
                product.getImage(),
                oldPrice,
                newPrice,
                invite.getStatus().toString(),
                sender.getName(),
                sender.getProfileImageUrl(),
                receiver.getName(),
                receiver.getProfileImageUrl()
        );
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

            //System.out.println("⏳ تم تحديث الدعوات المنتهية تلقائيًا.");
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
                .productId(invitation.getProductId())
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
                .map(this::mapToDtoSender)
                .toList();
    }


    private GroupPurchaseInviteResponse mapToDto(GroupPurchaseInvite invite) {
        boolean currentIsSender = invite.getSender().getId().equals(invite.getReceiver().getId());
        var other = currentIsSender ? invite.getReceiver() : invite.getReceiver();

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

    private GroupPurchaseInviteResponse mapToDtoSender(GroupPurchaseInvite invite) {
        boolean currentIsSender = invite.getSender().getId().equals(invite.getReceiver().getId());
        var other = currentIsSender ? invite.getSender() : invite.getSender();

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
