package geekcode.takatuf.Controller;

import geekcode.takatuf.Entity.User;
import geekcode.takatuf.Repository.UserRepository;
import geekcode.takatuf.Service.OrderService;
import geekcode.takatuf.dto.order.PlaceOrderRequest;
import geekcode.takatuf.dto.order.OrderResponse;
import geekcode.takatuf.dto.MessageResponse;
import geekcode.takatuf.dto.order.CustomOrderDecisionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    private Long extractUserId(UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    @PostMapping("/place")
    public ResponseEntity<OrderResponse> placeOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PlaceOrderRequest orderRequest) {

        Long userId = extractUserId(userDetails);
        OrderResponse response = orderService.placeOrder(userId, orderRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<MessageResponse> cancelOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {

        Long userId = extractUserId(userDetails);
        orderService.cancelOrder(userId, orderId);
        return ResponseEntity.ok(new MessageResponse("Order cancelled successfully"));
    }

    @GetMapping("/tracking/{orderId}")
    public ResponseEntity<OrderResponse> trackOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {

        extractUserId(userDetails);
        return ResponseEntity.ok(orderService.trackOrder(orderId));
    }

    @PostMapping("/custom/place")
    public ResponseEntity<OrderResponse> placeCustomOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PlaceOrderRequest request) {

        Long userId = extractUserId(userDetails);
        OrderResponse response = orderService.placeCustomOrder(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/custom/decide/{orderId}")
    public ResponseEntity<MessageResponse> decideCustomOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId,
            @RequestBody CustomOrderDecisionRequest request) {

        Long sellerId = extractUserId(userDetails);
        orderService.decideCustomOrder(sellerId, orderId, request);
        return ResponseEntity.ok(new MessageResponse("Custom order decision processed"));
    }

}
