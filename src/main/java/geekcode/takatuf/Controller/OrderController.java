package geekcode.takatuf.Controller;

import geekcode.takatuf.Entity.User;
import geekcode.takatuf.Repository.UserRepository;
import geekcode.takatuf.Service.OrderService;
import geekcode.takatuf.dto.order.PlaceOrderRequest;
import geekcode.takatuf.dto.order.OrderResponse;
import geekcode.takatuf.dto.MessageResponse;
import geekcode.takatuf.dto.order.CustomOrderDecisionRequest;
import geekcode.takatuf.Exception.Types.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    private Long getUserIdFromPrincipal(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .map(User::getId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
@PostMapping("/place")
public ResponseEntity<OrderResponse> placeOrder(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody PlaceOrderRequest orderRequest) {

    Long userId = getUserIdFromPrincipal(userDetails);
    OrderResponse response = orderService.placeOrder(userId, orderRequest);
    return ResponseEntity.ok(response);
}


    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<MessageResponse> cancelOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {

        Long userId = getUserIdFromPrincipal(userDetails);
        orderService.cancelOrder(userId, orderId);
        return ResponseEntity.ok(new MessageResponse("Order cancelled successfully"));
    }

    @GetMapping("/tracking/{orderId}")
    public ResponseEntity<OrderResponse> trackOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {

        getUserIdFromPrincipal(userDetails);
        return ResponseEntity.ok(orderService.trackOrder(orderId));
    }

    @PostMapping("/custom/place")
    public ResponseEntity<OrderResponse> placeCustomOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PlaceOrderRequest request) {

        Long userId = getUserIdFromPrincipal(userDetails);
        OrderResponse response = orderService.placeCustomOrder(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/custom/decide/{orderId}")
    public ResponseEntity<MessageResponse> decideCustomOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId,
            @Valid @RequestBody CustomOrderDecisionRequest request) {

        Long sellerId = getUserIdFromPrincipal(userDetails);
        orderService.decideCustomOrder(sellerId, orderId, request);
        return ResponseEntity.ok(new MessageResponse("Custom order decision processed"));
    }
}
