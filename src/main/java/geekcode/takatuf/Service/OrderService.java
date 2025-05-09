package geekcode.takatuf.Service;

import geekcode.takatuf.Entity.*;
import geekcode.takatuf.Exception.Types.ResourceNotFoundException;
import geekcode.takatuf.Repository.*;
import geekcode.takatuf.Enums.OrderStatus;
import geekcode.takatuf.Enums.OrderType;
import geekcode.takatuf.Enums.TrackingInfo;
import geekcode.takatuf.dto.order.PlaceOrderRequest;
import geekcode.takatuf.dto.order.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

        private final OrderRepository orderRepository;
        private final StoreRepository storeRepository;
        private final ProductRepository productRepository;
        private final OrderItemRepository orderItemRepository;
        private final UserRepository userRepository;

        @Transactional
        public OrderResponse placeOrder(Long userId, PlaceOrderRequest request) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

                Store store = storeRepository.findById(request.getStoreId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Store not found: " + request.getStoreId()));

                BigDecimal totalPrice = request.getItems().stream()
                                .map(item -> {
                                        Product product = productRepository.findById(item.getProductId())
                                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                                        "Product not found: " + item.getProductId()));
                                        return product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                                })
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                Order order = Order.builder()
                                .user(user)
                                .store(store)
                                .status(OrderStatus.PLACED)
                                .trackingInfo(TrackingInfo.PROCESSING)
                                .paymentMethod(request.getPaymentMethod())
                                .totalPrice(totalPrice)
                                .orderType(request.getOrderType())
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                Order savedOrder = orderRepository.save(order);

                List<OrderItem> orderItems = request.getItems().stream().map(itemReq -> {
                        Product product = productRepository.findById(itemReq.getProductId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Product not found: " + itemReq.getProductId()));

                        return OrderItem.builder()
                                        .product(product)
                                        .quantity(itemReq.getQuantity())
                                        .price(product.getPrice())
                                        .address(request.getAddress())
                                        .orderDate(LocalDateTime.now())
                                        .status(OrderStatus.PLACED)
                                        .order(savedOrder)
                                        .build();
                }).collect(Collectors.toList());

                orderItemRepository.saveAll(orderItems);

                return OrderResponse.builder()
                                .orderId(savedOrder.getId())
                                .status(savedOrder.getStatus())
                                .totalPrice(savedOrder.getTotalPrice())
                                .paymentMethod(savedOrder.getPaymentMethod())
                                .trackingInfo(savedOrder.getTrackingInfo())
                                .orderType(savedOrder.getOrderType())
                                .createdAt(savedOrder.getCreatedAt())
                                .updatedAt(savedOrder.getUpdatedAt())
                                .items(orderItems.stream().map(item -> OrderResponse.OrderItemResponse.builder()
                                                .productId(item.getProduct().getId())
                                                .productName(item.getProduct().getName())
                                                .quantity(item.getQuantity())
                                                .price(item.getPrice())
                                                .build()).collect(Collectors.toList()))
                                .build();
        }

        @Transactional
        public void cancelOrder(Long userId, Long orderId) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

                if (!order.getUser().getId().equals(userId)) {
                        throw new RuntimeException("Unauthorized to cancel this order");
                }

                if (order.getStatus() != OrderStatus.PLACED) {
                        throw new RuntimeException("Only placed orders can be cancelled");
                }

                order.setStatus(OrderStatus.CANCELLED);
                order.setTrackingInfo(TrackingInfo.CANCELLED_BY_USER);
                order.setUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);

                order.getOrderItems().forEach(item -> item.setStatus(OrderStatus.CANCELLED));
                orderItemRepository.saveAll(order.getOrderItems());
        }
}
