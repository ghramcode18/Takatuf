package geekcode.takatuf.Controller;

import geekcode.takatuf.Entity.GroupPurchaseInvite;
import geekcode.takatuf.Entity.GroupPurchaseOrder;
import geekcode.takatuf.Entity.Message;
import geekcode.takatuf.Entity.User;
import geekcode.takatuf.Exception.Types.ResourceNotFoundException;
import geekcode.takatuf.Repository.UserRepository;
import geekcode.takatuf.Service.GroupPurchaseService;
import geekcode.takatuf.dto.GroupPurchaseInviteResponse;
import geekcode.takatuf.dto.MessageResponse;
import geekcode.takatuf.dto.group_purchase.GroupPurchaseInviteDetailsResponse;
import geekcode.takatuf.dto.group_purchase.SendInviteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/group-purchase")
@RequiredArgsConstructor
public class GroupPurchaseInviteController {

    private final GroupPurchaseService groupPurchaseService;

    private final UserRepository userRepository;



    @PostMapping("/invite")
    public ResponseEntity<GroupPurchaseInvite> sendInvite(@AuthenticationPrincipal UserDetails userDetails,@RequestBody SendInviteRequest request) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        GroupPurchaseInvite invite = groupPurchaseService.sendInvite(request);
        return ResponseEntity.ok(invite);
    }

    @GetMapping("/invitations/{inviteId}")
    public ResponseEntity<GroupPurchaseInviteDetailsResponse> getInviteDetails(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long inviteId) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        GroupPurchaseInviteDetailsResponse details = groupPurchaseService.getInviteDetails(inviteId);
        return ResponseEntity.ok(details);
    }


    @PostMapping("/invitations/{invitationId}/accept")
    public ResponseEntity<MessageResponse> acceptInvitation(
            @PathVariable Long invitationId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        groupPurchaseService.acceptGroupPurchaseInvitation(invitationId, user.getId());
        return ResponseEntity.ok(new MessageResponse("Invitation accepted and order created"));
    }
    @PostMapping("/invitations/{invitationId}/reject")
    public ResponseEntity<MessageResponse> rejectInvitation(
            @PathVariable Long invitationId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        groupPurchaseService.rejectGroupPurchaseInvitation(invitationId, user.getId());
        return ResponseEntity.ok(new MessageResponse("Invitation rejected"));
    }
    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<MessageResponse> cancelOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        groupPurchaseService.cancelGroupPurchaseOrder(orderId,user.getId());
        return ResponseEntity.ok(new MessageResponse("Group purchase order cancelled successfully"));
    }


    @GetMapping("/invites/sent")
    public ResponseEntity<List<GroupPurchaseInviteResponse>> sentInvites(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return ResponseEntity.ok(groupPurchaseService.getSentInvites( user.getId()));
    }


    @GetMapping("/invites/received")
    public ResponseEntity<List<GroupPurchaseInviteResponse>> receivedInvites(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return ResponseEntity.ok(groupPurchaseService.getReceivedInvites( user.getId()));
    }

}
