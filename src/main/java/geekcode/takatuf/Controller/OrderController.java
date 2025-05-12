package geekcode.takatuf.Controller;

import geekcode.takatuf.Entity.User;
import geekcode.takatuf.Repository.UserRepository;
import geekcode.takatuf.Service.OrderService;
import geekcode.takatuf.dto.order.PlaceOrderRequest;
import geekcode.takatuf.dto.order.OrderResponse;
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

    @PostMapping("/place")
    public ResponseEntity<OrderResponse> placeOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PlaceOrderRequest orderRequest) {

        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long userId = user.getId();

        OrderResponse response = orderService.placeOrder(userId, orderRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<Void> cancelOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {

        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long userId = user.getId();

        orderService.cancelOrder(userId, orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tracking/{orderId}")
    public ResponseEntity<OrderResponse> trackOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.trackOrder(orderId));
    }

}
