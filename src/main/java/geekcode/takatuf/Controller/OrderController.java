package geekcode.takatuf.Controller;

import geekcode.takatuf.Entity.User;
import geekcode.takatuf.Repository.UserRepository;
import geekcode.takatuf.Service.OrderService;
import geekcode.takatuf.dto.order.PlaceOrderRequest;
import geekcode.takatuf.dto.order.CustomOrderResponse;
import geekcode.takatuf.dto.order.OrderResponse;
import geekcode.takatuf.dto.order.PlaceCustomOrderDecisionRequest;
import geekcode.takatuf.dto.order.PlaceCustomOrderRequest;
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
    public ResponseEntity<Void> cancelOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {

        Long userId = extractUserId(userDetails);
        orderService.cancelOrder(userId, orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tracking/{orderId}")
    public ResponseEntity<OrderResponse> trackOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.trackOrder(orderId));
    }

    @PostMapping("/custom-orders/place")
    public ResponseEntity<CustomOrderResponse> placeCustomOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PlaceCustomOrderRequest request) {

        Long userId = extractUserId(userDetails);
        CustomOrderResponse response = orderService.placeCustomOrder(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/custom-orders/decide/{customOrderId}")
    public ResponseEntity<CustomOrderResponse> decideCustomOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long customOrderId,
            @RequestBody PlaceCustomOrderDecisionRequest request) {

        Long sellerId = extractUserId(userDetails);
        CustomOrderResponse response = orderService.decideCustomOrder(sellerId, customOrderId, request);
        return ResponseEntity.ok(response);
    }

}
