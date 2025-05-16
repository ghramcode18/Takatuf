package geekcode.takatuf.Service;

import geekcode.takatuf.Entity.*;
import geekcode.takatuf.Exception.Types.ResourceNotFoundException;
import geekcode.takatuf.Exception.Types.UnauthorizedException;
import geekcode.takatuf.Repository.*;
import geekcode.takatuf.Enums.OrderStatus;
import geekcode.takatuf.Enums.OrderType;
import geekcode.takatuf.Enums.TrackingInfo;
import geekcode.takatuf.dto.order.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

                if (request.getItems() == null || request.getItems().isEmpty()) {
                        throw new ResourceNotFoundException("Order must contain at least one product");
                }

                Product firstProduct = productRepository.findById(request.getItems().get(0).getProductId())
                                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
                Store store = firstProduct.getStore();

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
                                .orderType(OrderType.STANDARD)
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
                        throw new UnauthorizedException("Unauthorized to cancel this order");
                }

                if (order.getStatus() != OrderStatus.PLACED) {
                        throw new RuntimeException("Only placed orders can be cancelled");
                }

                order.setStatus(OrderStatus.CANCELLED);
                order.setTrackingInfo(TrackingInfo.CANCELLED_BY_USER);
                order.setUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);

                if (order.getOrderItems() != null) {
                        order.getOrderItems().forEach(item -> item.setStatus(OrderStatus.CANCELLED));
                        orderItemRepository.saveAll(order.getOrderItems());
                }
        }

        public OrderResponse trackOrder(Long orderId) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String username = authentication.getName();

                User user = userRepository.findByEmail(username)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

                if (!order.getUser().getId().equals(user.getId())) {
                        throw new UnauthorizedException("Unauthorized to view this order");
                }

                return OrderResponse.builder()
                                .orderId(order.getId())
                                .status(order.getStatus())
                                .trackingInfo(order.getTrackingInfo())
                                .totalPrice(order.getTotalPrice())
                                .paymentMethod(order.getPaymentMethod())
                                .orderType(order.getOrderType())
                                .createdAt(order.getCreatedAt())
                                .updatedAt(order.getUpdatedAt())
                                .items(order.getOrderItems() == null ? List.of()
                                                : order.getOrderItems().stream()
                                                                .map(item -> OrderResponse.OrderItemResponse.builder()
                                                                                .productId(item.getProduct().getId())
                                                                                .productName(item.getProduct()
                                                                                                .getName())
                                                                                .quantity(item.getQuantity())
                                                                                .price(item.getPrice())
                                                                                .build())
                                                                .collect(Collectors.toList()))
                                .build();
        }

        @Transactional
        public OrderResponse placeCustomOrder(Long userId, PlaceOrderRequest request) {
                if (request.getOrderType() != OrderType.CUSTOM) {
                        throw new RuntimeException("Order type must be CUSTOM for custom orders");
                }

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

                Store store = storeRepository.findById(request.getStoreId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Store not found: " + request.getStoreId()));

                Order customOrder = Order.builder()
                                .user(user)
                                .store(store)
                                .category(request.getCategory())
                                .customizationDetails(request.getCustomizationDetails())
                                .status(OrderStatus.PLACED)
                                .trackingInfo(TrackingInfo.PROCESSING)
                                .orderType(OrderType.CUSTOM)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                Order savedOrder = orderRepository.save(customOrder);
                return mapToOrderResponse(savedOrder);
        }

        @Transactional
        public OrderResponse decideCustomOrder(Long sellerId, Long orderId, CustomOrderDecisionRequest request) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new ResourceNotFoundException("Custom Order not found: " + orderId));

                if (order.getOrderType() != OrderType.CUSTOM) {
                        throw new RuntimeException("Not a custom order");
                }

                Store store = order.getStore();
                if (!store.getOwner().getId().equals(sellerId)) {
                        throw new UnauthorizedException("Unauthorized to decide on this custom order");
                }

                if (order.getStatus() != OrderStatus.PLACED) {
                        throw new RuntimeException("Custom order already processed");
                }

                if (Boolean.TRUE.equals(request.getAccept())) {
                        if (request.getProposedPrice() == null
                                        || request.getProposedPrice().compareTo(BigDecimal.ZERO) <= 0) {
                                throw new RuntimeException(
                                                "Proposed price must be provided and positive when accepting the order");
                        }

                        order.setStatus(OrderStatus.ACCEPTED);
                        order.setTrackingInfo(TrackingInfo.ACCEPTED_BY_STORE);
                        order.setProposedPrice(request.getProposedPrice());
                } else {
                        order.setStatus(OrderStatus.REJECTED);
                        order.setTrackingInfo(TrackingInfo.REJECTED_BY_STORE);
                }

                order.setUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);

                return mapToOrderResponse(order);
        }

        private OrderResponse mapToOrderResponse(Order order) {
                return OrderResponse.builder()
                                .orderId(order.getId())
                                .status(order.getStatus())
                                .trackingInfo(order.getTrackingInfo())
                                .totalPrice(order.getOrderType() == OrderType.CUSTOM ? order.getProposedPrice()
                                                : order.getTotalPrice())
                                .paymentMethod(order.getPaymentMethod())
                                .orderType(order.getOrderType())
                                .createdAt(order.getCreatedAt())
                                .updatedAt(order.getUpdatedAt())
                                .category(order.getOrderType() == OrderType.CUSTOM ? order.getCategory() : null)
                                .customizationDetails(order.getOrderType() == OrderType.CUSTOM
                                                ? order.getCustomizationDetails()
                                                : null)
                                .proposedPrice(order.getOrderType() == OrderType.CUSTOM ? order.getProposedPrice()
                                                : null)
                                .items(order.getOrderItems() == null ? List.of()
                                                : order.getOrderItems().stream()
                                                                .map(item -> OrderResponse.OrderItemResponse.builder()
                                                                                .productId(item.getProduct().getId())
                                                                                .productName(item.getProduct()
                                                                                                .getName())
                                                                                .quantity(item.getQuantity())
                                                                                .price(item.getPrice())
                                                                                .build())
                                                                .collect(Collectors.toList()))
                                .build();
        }

}
