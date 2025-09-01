package geekcode.takatuf.Controller;

import geekcode.takatuf.Entity.Order;
import geekcode.takatuf.Entity.User;
import geekcode.takatuf.Enums.PaymentMethod;
import geekcode.takatuf.Repository.UserRepository;
import geekcode.takatuf.Service.OrderService;
import geekcode.takatuf.dto.order.*;
import geekcode.takatuf.dto.order.OfferDto.BuyerOfferDecisionRequest;
import geekcode.takatuf.dto.order.OfferDto.OfferResponse;
import geekcode.takatuf.dto.order.OfferDto.SubmitOfferRequest;
import geekcode.takatuf.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import geekcode.takatuf.Exception.Types.ResourceNotFoundException;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    private Long extractUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .map(User::getId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @PostMapping(value = "/custom-order", consumes = { "multipart/form-data" })
    public ResponseEntity<OrderResponse> placeCustomOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute PlaceOrderRequest request) {
        Long userId = extractUserId(userDetails);
        OrderResponse response = orderService.placeCustomOrder(userId, request);
        return ResponseEntity.ok(response);
    }

     @PostMapping(value = "/custom-order/address")
     public ResponseEntity<Long> updateCustomOrderAddress(
             @AuthenticationPrincipal UserDetails userDetails,
             @RequestParam Long OrderId,
             @RequestBody AddressRequest addressRequest) {
         Long userId = extractUserId(userDetails);
         orderService.updateCustomOrderAddress(OrderId, addressRequest);
         return ResponseEntity.ok(OrderId);
     }

     @PostMapping(value = "/custom-order/payment")
     public ResponseEntity<Long> updateCustomOrderPayment(
             @AuthenticationPrincipal UserDetails userDetails,
             @RequestParam Long OrderId,
             @RequestParam PaymentMethod paymentMethod) {
         Long userId = extractUserId(userDetails);
         orderService.updateCustomOrderPayment(OrderId, paymentMethod);
         return ResponseEntity.ok(OrderId);
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

    @PostMapping("/custom-orders/{orderId}/offers")
    public ResponseEntity<?> submitCustomOrderOffer(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId,
            @RequestBody SubmitOfferRequest request) {
        Long sellerId = extractUserId(userDetails);
        orderService.submitOffer(sellerId, orderId, request);
        return ResponseEntity.ok(new MessageResponse("Offer submitted successfully"));
    }

    @GetMapping("/custom-orders/{orderId}/offers")
    public ResponseEntity<List<OfferResponse>> viewCustomOrderOffers(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {
        Long buyerId = extractUserId(userDetails);
        return ResponseEntity.ok(orderService.getOffersForOrder(buyerId, orderId));
    }

    @PostMapping("/custom-orders/offers/{offerId}/respond")
    public ResponseEntity<?> respondToCustomOrderOffer(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long offerId,
            @RequestBody BuyerOfferDecisionRequest request) {
        Long buyerId = extractUserId(userDetails);
        orderService.respondToOffer(buyerId, offerId, request);
        return ResponseEntity.ok(new MessageResponse("Buyer response recorded"));
    }

    @GetMapping("/card")
    public ResponseEntity<PendingOrderResponse> getPendingOrder(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = extractUserId(userDetails);
        return ResponseEntity.ok(orderService.getPendingOrder(userId));
    }

    @PostMapping("/pending-order")
    public ResponseEntity<Long> createOrGetPendingOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PlaceOrderRequest orderReques) {
        Long userId = extractUserId(userDetails);
        Long pendingOrderId = orderService.createOrGetPendingOrder(userId, orderReques);
        return ResponseEntity.ok(pendingOrderId);
    }

    @PutMapping("/pending-order/address")
    public ResponseEntity<Long> updatePendingOrderAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long pendingOrderId,
            @RequestBody AddressRequest addressRequest) {
        Long userId = extractUserId(userDetails);
        orderService.updatePendingOrderAddress(userId, pendingOrderId, addressRequest);
        return ResponseEntity.ok(pendingOrderId);
    }

    @PutMapping("/pending-order/payment")
    public ResponseEntity<Long> updatePendingOrderPayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long OrderId,
            @RequestParam PaymentMethod paymentMethod) {
        Long userId = extractUserId(userDetails);
        orderService.updatePendingOrderPayment(userId, OrderId, paymentMethod);
        return ResponseEntity.ok(OrderId);
    }

    @PostMapping("/pending-order/review")
    public ResponseEntity<PendingOrderReviewResponse> getPendingOrderReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long pendingOrderId) {
        Long userId = extractUserId(userDetails);
        PendingOrderReviewResponse response = orderService.getPendingOrderReview(userId, pendingOrderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/pending-order/confirm")
    public ResponseEntity<List<OrderResponse>> confirmPendingOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long pendingOrderId) {
        Long userId = extractUserId(userDetails);
        List<OrderResponse> response = orderService.confirmPendingOrder(userId, pendingOrderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/custom-orders/buyer")
    public ResponseEntity<List<OrderResponse>> getCustomOrdersByBuyer(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long buyerId = extractUserId(userDetails);
        return ResponseEntity.ok(orderService.getCustomOrdersByBuyer(buyerId));
    }

    @GetMapping("/custom-orders/seller")
    public ResponseEntity<List<OrderResponse>> getCustomOrdersForSeller(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long sellerId = extractUserId(userDetails);
        return ResponseEntity.ok(orderService.getCustomOrdersForSellerByCategory(sellerId));

    }

    @GetMapping("/custom-orders/{orderId}")
    public ResponseEntity<OrderResponse> getCustomOrderById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(orderService.getCustomOrderById(orderId, email));
    }

    @GetMapping("/getMyOrder")
    public ResponseEntity<List<OrderResponse>> getMyOrder(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = extractUserId(userDetails);
        List<OrderResponse> response = orderService.getMyOrder(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<List<OrderResponseById>> getOrderbyId(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = extractUserId(userDetails);
        List<OrderResponseById>response = orderService.getOrderById(userId,orderId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('SELLER') or hasRole('SELLER')")
    @PostMapping("/seller/{orderId}/status")
    public ResponseEntity<OrderResponse> sellerUpdateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateOrderStatusRequest body) {
        OrderResponse response = orderService.sellerUpdateOrderStatus(orderId, body);
        return ResponseEntity.ok(response);
    }

}