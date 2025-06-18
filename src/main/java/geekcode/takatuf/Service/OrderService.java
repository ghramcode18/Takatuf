package geekcode.takatuf.Service;

import geekcode.takatuf.Entity.*;
import geekcode.takatuf.Enums.PaymentMethod;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

        private final OrderRepository orderRepository;
        
        private final StoreRepository storeRepository;
        private final ProductRepository productRepository;
        private final OrderItemRepository orderItemRepository;
        private final UserRepository userRepository;

        private final PendingOrderItemRepository pendingOrderItemRepository;
        private final PendingOrderRepository pendingOrderRepository;
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

                Order customOrder = Order.builder()
                                .user(user)
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

        @Transactional
        public Long createOrGetPendingOrder(Long userId, PlaceOrderRequest request) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                // حذف الطلب المرحلي السابق (لو موجود)
                pendingOrderRepository.findByUserIdAndStatus(userId, OrderStatus.PENDING)
                        .ifPresent(existing -> {
                                pendingOrderItemRepository.deleteAllByPendingOrder(existing);
                                pendingOrderRepository.delete(existing);
                        });

                BigDecimal totalPrice = request.getItems().stream()
                        .map(item -> {
                                Product product = productRepository.findById(item.getProductId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                "Product not found: " + item.getProductId()));
                                return product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // إنشاء الطلب المرحلي (بدون store)
                PendingOrder newOrder = PendingOrder.builder()
                        .user(user)
                        .recipientName(user.getName())
                        .totalPrice(totalPrice)
                        .status(OrderStatus.PENDING)
                        .orderType(request.getOrderType())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

                PendingOrder savedOrder = pendingOrderRepository.save(newOrder);

                List<PendingOrderItem> items = request.getItems().stream()
                        .map(itemReq -> {
                                Product product = productRepository.findById(itemReq.getProductId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                "Product not found: " + itemReq.getProductId()));
                                return PendingOrderItem.builder()
                                        .pendingOrder(savedOrder)
                                        .product(product)
                                        .quantity(itemReq.getQuantity())
                                        .addedAt(LocalDateTime.now())
                                        .build();
                        }).toList();

                pendingOrderItemRepository.saveAll(items);

                return savedOrder.getId();
        }

        @Transactional
        public Long updatePendingOrderAddress(Long userId,Long pendingOrderId, AddressRequest addressRequest) {
                PendingOrder pendingOrder = pendingOrderRepository.findById(pendingOrderId)
                        .orElseThrow(() -> new ResourceNotFoundException("Pending order not found for user: " + userId));

                pendingOrder.setRecipientName(addressRequest.getFirstName() + " " + addressRequest.getLastName());
                pendingOrder.setRegion(addressRequest.getRegion());
                pendingOrder.setStreetName(addressRequest.getStreetName());
                pendingOrder.setBuildingNumber(addressRequest.getBuildingNumber());
                pendingOrder.setPhoneNumber(addressRequest.getPhoneNumber());
                pendingOrder.setUpdatedAt(LocalDateTime.now());
                pendingOrderRepository.save(pendingOrder);
                return pendingOrder.getId();
        }

        @Transactional
        public Long updatePendingOrderPayment(Long userId,Long pendingOrderId,PaymentMethod paymentMethod) {
                PendingOrder pendingOrder = pendingOrderRepository.findById(pendingOrderId)
                        .orElseThrow(() -> new ResourceNotFoundException("Pending order not found for user: " + userId));

                pendingOrder.setPaymentMethod(paymentMethod);
                pendingOrder.setUpdatedAt(LocalDateTime.now());

                 pendingOrderRepository.save(pendingOrder);
                return pendingOrder.getId();
        }

        public PendingOrderReviewResponse getPendingOrderReview(Long userId,Long pendingOrderId) {
                PendingOrder pendingOrder = pendingOrderRepository.findById(pendingOrderId)
                        .orElseThrow(() -> new ResourceNotFoundException("No pending order found for user: " + userId));

                        List<PendingOrderItem> items = pendingOrderItemRepository.findByPendingOrder(pendingOrder);

                return PendingOrderReviewResponse.builder()
                        .pendingOrderId(pendingOrder.getId())
                        .recipientName(pendingOrder.getRecipientName())
                        .region(pendingOrder.getRegion())
                        .streetName(pendingOrder.getStreetName())
                        .buildingNumber(pendingOrder.getBuildingNumber())
                        .phoneNumber(pendingOrder.getPhoneNumber())
                        .orderType(pendingOrder.getOrderType())
                        .paymentMethod(pendingOrder.getPaymentMethod())
                        .totalPrice(pendingOrder.getTotalPrice())
                        .items(items.stream().map(i -> PendingOrderReviewResponse.Item.builder()
                                .productId(i.getProduct().getId())
                                .productName(i.getProduct().getName())
                                .quantity(i.getQuantity())
                                .price(i.getProduct().getPrice())
                                .build()).toList())
                        .build();
        }

        @Transactional
        public List<OrderResponse> confirmPendingOrder(Long userId,Long pendingOrderId) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                // جلب الطلب المرحلي
                PendingOrder pendingOrder = pendingOrderRepository
                        .findById(pendingOrderId)
                        .orElseThrow(() -> new ResourceNotFoundException("No pending order found"));

                List<PendingOrderItem> items = pendingOrderItemRepository.findByPendingOrder(pendingOrder);

                // تقسيم العناصر حسب المتجر
                Map<Store, List<PendingOrderItem>> groupedByStore = items.stream()
                        .collect(Collectors.groupingBy(item -> item.getProduct().getStore()));

                List<OrderResponse> confirmedOrders = new ArrayList<>();

                for (Map.Entry<Store, List<PendingOrderItem>> entry : groupedByStore.entrySet()) {
                        Store store = entry.getKey();
                        List<PendingOrderItem> storeItems = entry.getValue();

                        BigDecimal totalPrice = storeItems.stream()
                                .map(i -> i.getProduct().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        Order order = Order.builder()
                                .user(user)
                                .store(store)
                                .status(OrderStatus.PLACED)
                                .trackingInfo(TrackingInfo.PROCESSING)
                                .totalPrice(totalPrice)
                                .orderType(pendingOrder.getOrderType())
                                .paymentMethod(pendingOrder.getPaymentMethod())
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                        orderRepository.save(order);

                        List<OrderItem> orderItems = new ArrayList<>();

                        for (PendingOrderItem item : storeItems) {
                                Product product = item.getProduct();

                                // تحديث كمية المنتج
                                if (product.getQuantity() < item.getQuantity()) {
                                        throw new RuntimeException("Not enough stock for product: " + product.getName());
                                }
                                product.setQuantity(product.getQuantity() - item.getQuantity());
                                productRepository.save(product);

                                OrderItem orderItem = OrderItem.builder()
                                        .order(order)
                                        .product(product)
                                        .quantity(item.getQuantity())
                                        .price(product.getPrice())
                                        .status(OrderStatus.PLACED)
                                        .orderDate(LocalDateTime.now())
//                                        .address(pendingOrder.getAddress()) // من الداتا المدخلة
                                        .build();

                                orderItems.add(orderItem);
                        }

                        orderItemRepository.saveAll(orderItems);

                        confirmedOrders.add(mapToOrderResponse(order,orderItems));
                }

                // حذف الطلب المرحلي
                pendingOrderItemRepository.deleteAllByPendingOrder(pendingOrder);
                pendingOrderRepository.delete(pendingOrder);

                return confirmedOrders;
        }

        private OrderResponse mapToOrderResponse(Order order, List<OrderItem> items) {
                return OrderResponse.builder()
                        .orderId(order.getId())
                        .status(order.getStatus())
                        .trackingInfo(order.getTrackingInfo())
                        .paymentMethod(order.getPaymentMethod())
                        .orderType(order.getOrderType())
                        .totalPrice(order.getTotalPrice())
                        .createdAt(order.getCreatedAt())
                        .updatedAt(order.getUpdatedAt())
                        .items(items.stream().map(i -> OrderResponse.OrderItemResponse.builder()
                                .productId(i.getProduct().getId())
                                .productName(i.getProduct().getName())
                                .quantity(i.getQuantity())
                                .price(i.getPrice())
                                .build()).toList())
                        .build();
        }

}
