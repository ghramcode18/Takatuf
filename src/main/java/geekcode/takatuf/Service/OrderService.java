package geekcode.takatuf.Service;

import geekcode.takatuf.Entity.*;
import geekcode.takatuf.Enums.PaymentMethod;
import geekcode.takatuf.Exception.Types.*;
import geekcode.takatuf.Repository.*;
import geekcode.takatuf.Enums.OfferStatus;
import geekcode.takatuf.Enums.OrderStatus;
import geekcode.takatuf.Enums.OrderType;
import geekcode.takatuf.Enums.UserType;
import geekcode.takatuf.Enums.TrackingInfo;
import geekcode.takatuf.dto.order.*;
import geekcode.takatuf.dto.order.OfferDto.BuyerOfferDecisionRequest;
import geekcode.takatuf.dto.order.OfferDto.OfferResponse;
import geekcode.takatuf.dto.order.OfferDto.SubmitOfferRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

        private final OrderRepository orderRepository;
        private final StoreRepository storeRepository;
        private final CustomOrderOfferRepository customOrderOfferRepository;
        private final ProductRepository productRepository;
        private final CategoryRepository categoryRepository;
        private final OrderItemRepository orderItemRepository;
        private final UserRepository userRepository;
        private final SellerCategoryRepository sellerCategoryRepository;
        private final PendingOrderItemRepository pendingOrderItemRepository;
        private final PendingOrderRepository pendingOrderRepository;
        private final String uploadDir = "uploads/custom_orders/";

        @Transactional
        public OrderResponse placeCustomOrder(Long userId, PlaceOrderRequest request) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

                if (user.getType() != UserType.BUYER) {
                        throw new UnauthorizedException("Only buyers can place custom orders");
                }

                if (request.getOrderType() != OrderType.CUSTOM) {
                        throw new RuntimeException("Order type must be CUSTOM for custom orders");
                }

                if (request.getBuyerProposedPrice() == null
                                || request.getBuyerProposedPrice().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new RuntimeException("Buyer proposed price must be provided and positive");
                }

                Category category = null;
                if (request.getCategoryId() != null) {
                        category = categoryRepository.findById(request.getCategoryId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Category not found: " + request.getCategoryId()));
                }

                String imagePath = null;
                MultipartFile imageFile = request.getImageFile();
                if (imageFile != null && !imageFile.isEmpty()) {
                        imagePath = saveCustomOrderImage(imageFile);
                }

                Order customOrder = Order.builder()
                                .user(user)
                                .category(category)
                                .name(request.getName())
                                .customizationDetails(request.getCustomizationDetails())
                                .imageUrl(imagePath)
                                .buyerProposedPrice(request.getBuyerProposedPrice())
                                .status(OrderStatus.PLACED)
                                .trackingInfo(TrackingInfo.PROCESSING)
                                .orderType(OrderType.CUSTOM)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                Order savedOrder = orderRepository.save(customOrder);
                return mapToOrderResponse(savedOrder);
        }

        private String saveCustomOrderImage(MultipartFile file) {
                try {
                        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                        Path uploadPath = Paths.get("uploads/custom_orders/");

                        if (!Files.exists(uploadPath)) {
                                Files.createDirectories(uploadPath);
                        }

                        Path filePath = uploadPath.resolve(fileName);
                        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                        return ServletUriComponentsBuilder.fromCurrentContextPath()
                                        .path("/uploads/custom_orders/")
                                        .path(fileName)
                                        .toUriString();

                } catch (IOException e) {
                        throw new BadRequestException("Failed to store image");
                }
        }

        @Transactional
        public void cancelOrder(Long userId, Long orderId) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

                if (!order.getUser().getId().equals(userId)) {
                        throw new UnauthorizedException("Unauthorized to cancel this order");
                }

                // Allow cancel only if not accepted/rejected/cancelled yet
                if (order.getStatus() == OrderStatus.ACCEPTED
                                || order.getStatus() == OrderStatus.REJECTED
                                || order.getStatus() == OrderStatus.CANCELLED) {
                        throw new RuntimeException("Cannot cancel an order that is already finalized");
                }

                order.setStatus(OrderStatus.CANCELLED);
                order.setTrackingInfo(TrackingInfo.CANCELLED_BY_USER);
                order.setUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);
        }

        @Transactional
        public void submitOffer(Long sellerId, Long orderId, SubmitOfferRequest request) {
                User seller = userRepository.findById(sellerId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

                if (seller.getType() != UserType.SELLER)
                        throw new UnauthorizedException("Only sellers can submit offers");

                CustomOrderOffer offer = CustomOrderOffer.builder()
                                .order(order)
                                .seller(seller)
                                .proposedPrice(request.getProposedPrice())
                                .additionalInfo(request.getAdditionalInfo())
                                .status(OfferStatus.ACTIVE)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                customOrderOfferRepository.save(offer);
        }

        public List<OfferResponse> getOffersForOrder(Long buyerId, Long orderId) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

                if (!order.getUser().getId().equals(buyerId))
                        throw new UnauthorizedException("Unauthorized to view offers");

                return customOrderOfferRepository.findByOrderId(orderId).stream()
                                .map(offer -> {
                                        OfferResponse resp = new OfferResponse();
                                        resp.setOfferId(offer.getId());
                                        resp.setSellerId(offer.getSeller().getId());
                                        resp.setSellerName(offer.getSeller().getName());
                                        resp.setProposedPrice(offer.getProposedPrice());
                                        resp.setAdditionalInfo(offer.getAdditionalInfo());
                                        resp.setStatus(offer.getStatus());
                                        return resp;
                                }).toList();
        }

        @Transactional
        public void respondToOffer(Long buyerId, Long offerId, BuyerOfferDecisionRequest request) {
                CustomOrderOffer offer = customOrderOfferRepository.findById(offerId)
                                .orElseThrow(() -> new ResourceNotFoundException("Offer not found"));

                Order order = offer.getOrder();

                if (!order.getUser().getId().equals(buyerId))
                        throw new UnauthorizedException("Unauthorized to respond to this offer");

                if (order.getStatus() == OrderStatus.ACCEPTED || order.getStatus() == OrderStatus.REJECTED)
                        throw new RuntimeException("Order already finalized");

                if (request.isAccept()) {
                        if (order.getBuyerProposedPrice().compareTo(offer.getProposedPrice()) != 0) {
                                order.setBuyerProposedPrice(offer.getProposedPrice());
                        }

                        order.setStatus(OrderStatus.ACCEPTED);
                        order.setTrackingInfo(TrackingInfo.ACCEPTED_BY_BUYER);
                        order.setProposedPrice(offer.getProposedPrice());
                        offer.setStatus(OfferStatus.ACTIVE);

                } else {
                        offer.setStatus(OfferStatus.REJECTED);
                        order.setTrackingInfo(TrackingInfo.REJECTED_BY_BUYER);
                }

                offer.setUpdatedAt(LocalDateTime.now());
                order.setUpdatedAt(LocalDateTime.now());
                customOrderOfferRepository.save(offer);
                orderRepository.save(order);
        }

        public OrderResponse trackOrder(Long orderId) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String username = authentication.getName();

                User user = userRepository.findByEmail(username)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

                if (user.getType() == UserType.BUYER) {
                        if (!order.getUser().getId().equals(user.getId())) {
                                throw new UnauthorizedException("Buyers can only track their own orders");
                        }
                } else if (user.getType() != UserType.SELLER) {

                        throw new UnauthorizedException("Only buyers and sellers can track orders");
                }

                return mapToOrderResponse(order);
        }

        private OrderResponse mapToOrderResponse(Order order) {
                List<OrderItem> items = orderItemRepository.findByOrder_Id(order.getId());
                User buyer = order.getUser();
                return OrderResponse.builder()
                                .orderId(order.getId())
                                .name(order.getName())
                                .status(order.getStatus())
                                .trackingInfo(order.getTrackingInfo())
                                .totalPrice(order.getTotalPrice())
                                .orderType(order.getOrderType())
                                .createdAt(order.getCreatedAt())
                                .updatedAt(order.getUpdatedAt())
                                .items(items != null ? items.stream().map(this::mapToOrderItemDto)
                                                .collect(Collectors.toList()) : Collections.emptyList())
                                .customizationDetails(order.getCustomizationDetails())
                                .imageUrl(order.getImageUrl())
                                .buyerProposedPrice(order.getBuyerProposedPrice())
                                .proposedPrice(order.getProposedPrice())
                                .categoryId(order.getCategory() != null ? order.getCategory().getId() : null)
                                .categoryName(order.getCategory() != null ? order.getCategory().getName() : null)
                                .buyerName(buyer.getName())
                                .buyerImageUrl(buyer.getProfileImageUrl())
                                .build();
        }

        private OrderResponse.OrderItemResponse mapToOrderItemDto(OrderItem orderItem) {
                return OrderResponse.OrderItemResponse.builder()
                                .productId(orderItem.getProduct().getId())
                                .productName(orderItem.getProduct().getName())
                                .quantity(orderItem.getQuantity())
                                .price(orderItem.getPrice())
                                .build();
        }

        public OrderResponse getCustomOrderById(Long orderId, String username) {
                User user = userRepository.findByEmail(username)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

                boolean isOwner = order.getUser().getId().equals(user.getId());
                boolean isSellerWithOffer = customOrderOfferRepository.existsByOrderIdAndSellerEmail(orderId, username);

                if (!isOwner && !isSellerWithOffer)
                        throw new UnauthorizedException("You are not allowed to view this order");

                return mapToOrderResponse(order);
        }

        public List<OrderResponse> getCustomOrdersByBuyer(Long buyerId) {
                List<Order> orders = orderRepository.findByUserIdAndOrderType(buyerId, OrderType.CUSTOM);
                return orders.stream().map(this::mapToOrderResponse).toList();
        }

        public List<OrderResponse> getCustomOrdersForSellerByCategory(Long sellerId) {
                List<Long> categoryIds = sellerCategoryRepository.findBySeller_Id(sellerId)
                                .stream()
                                .map(sc -> sc.getCategory().getId())
                                .toList();

                List<Order> matchingOrders = orderRepository.findByOrderType(OrderType.CUSTOM).stream()
                                .filter(order -> order.getCategory() != null
                                                && categoryIds.contains(order.getCategory().getId())
                                                && !customOrderOfferRepository.existsByOrderIdAndSellerId(order.getId(),
                                                                sellerId))
                                .toList();

                return matchingOrders.stream().map(this::mapToOrderResponse).toList();
        }

        @Transactional
        public Long createOrGetPendingOrder(Long userId, PlaceOrderRequest request) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

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
                                                                        "Product not found: "
                                                                                        + itemReq.getProductId()));
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

        public PendingOrderResponse getPendingOrder(Long userId) {
                PendingOrder order = pendingOrderRepository.findByUserIdAndStatus(userId, OrderStatus.PENDING)
                                .orElseThrow(() -> new ResourceNotFoundException("No pending order found"));

                List<PendingOrderItem> items = pendingOrderItemRepository.findByPendingOrder(order);

                BigDecimal total = BigDecimal.ZERO;

                List<PendingOrderItemResponse> itemResponses = new ArrayList<>();
                for (PendingOrderItem item : items) {
                        Product product = productRepository.findById(item.getProduct().getId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Product not found: " + item.getProduct().getId()));
                        BigDecimal currentPrice = product.getPrice();
                        BigDecimal itemTotal = currentPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

                        item.setPrice(currentPrice);
                        total = total.add(itemTotal);

                        itemResponses.add(new PendingOrderItemResponse(
                                        product.getId(),
                                        product.getName(),
                                        currentPrice,
                                        item.getQuantity()));
                }

                order.setTotalPrice(total);
                pendingOrderItemRepository.saveAll(items);
                pendingOrderRepository.save(order);

                return new PendingOrderResponse(order.getId(), total, itemResponses);
        }

        @Transactional
        public Long updatePendingOrderAddress(Long userId, Long pendingOrderId, AddressRequest addressRequest) {
                PendingOrder pendingOrder = pendingOrderRepository.findById(pendingOrderId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Pending order not found for user: " + userId));

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
        public Long updatePendingOrderPayment(Long userId, Long pendingOrderId, PaymentMethod paymentMethod) {
                PendingOrder pendingOrder = pendingOrderRepository.findById(pendingOrderId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Pending order not found for user: " + userId));

                pendingOrder.setPaymentMethod(paymentMethod);
                pendingOrder.setUpdatedAt(LocalDateTime.now());

                pendingOrderRepository.save(pendingOrder);
                return pendingOrder.getId();
        }

        public PendingOrderReviewResponse getPendingOrderReview(Long userId, Long pendingOrderId) {
                PendingOrder pendingOrder = pendingOrderRepository.findById(pendingOrderId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "No pending order found for user: " + userId));

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
        public List<OrderResponse> confirmPendingOrder(Long userId, Long pendingOrderId) {
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
                                        .map(i -> i.getProduct().getPrice()
                                                        .multiply(BigDecimal.valueOf(i.getQuantity())))
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
                                        throw new RuntimeException(
                                                        "Not enough stock for product: " + product.getName());
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
                                                // .address(pendingOrder.getAddress()) // من الداتا المدخلة
                                                .build();

                                orderItems.add(orderItem);
                        }

                        orderItemRepository.saveAll(orderItems);

                        confirmedOrders.add(mapToOrderResponse(order, orderItems));
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

        public List<Order> getMyOrder(Long userId) {

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                List<Order> myOrder = orderRepository.findByUserId(userId);
                return myOrder;
        }

        public List<Order> getOrderbyId(Long userId, Long orderId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                List<Order> myOrder = orderRepository.findByUserIdAndId(userId, orderId);
                return myOrder;
        }
}
