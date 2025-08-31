package geekcode.takatuf.Service;

import geekcode.takatuf.Entity.Product;
import geekcode.takatuf.Entity.ProductPromotion;
import geekcode.takatuf.Entity.User;
import geekcode.takatuf.Enums.PromotionStatus;
import geekcode.takatuf.Enums.UserType;
import geekcode.takatuf.Exception.Types.BadRequestException;
import geekcode.takatuf.Exception.Types.UnauthorizedException;
import geekcode.takatuf.Repository.ProductPromotionRepository;
import geekcode.takatuf.Repository.ProductRepository;
import geekcode.takatuf.Repository.UserRepository;
import geekcode.takatuf.dto.PromotionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final ProductPromotionRepository promotionRepo;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    private PromotionResponse toResponse(ProductPromotion promotion) {
        return PromotionResponse.builder()
                .id(promotion.getId())
                .productId(promotion.getProduct().getId())
                .productName(promotion.getProduct().getName())
                .productImage(promotion.getProduct().getImage())
                .storeId(promotion.getProduct().getStore().getId())
                .storeName(promotion.getProduct().getStore().getName())
                .sellerId(promotion.getSeller().getId())
                .sellerName(promotion.getSeller().getName())
                .status(promotion.getStatus().name())
                .durationDays(promotion.getDurationDays())
                .createdAt(promotion.getCreatedAt())
                .decidedAt(promotion.getDecidedAt())
                .startAt(promotion.getStartAt())
                .endAt(promotion.getEndAt())
                .build();
    }

    @Transactional
    public PromotionResponse createRequest(String sellerEmail, Long productId, Integer durationDays) {
        if (durationDays == null || durationDays <= 0 || durationDays > 30) {
            throw new BadRequestException("Invalid duration");
        }

        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new BadRequestException("Seller not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Product not found"));

        if (!product.getStore().getOwner().getId().equals(seller.getId())) {
            throw new UnauthorizedException("Not your product");
        }

        if (promotionRepo.existsByProduct_IdAndStatus(productId, PromotionStatus.PENDING)) {
            throw new BadRequestException("Already has pending request");
        }

        if (promotionRepo.existsByProduct_IdAndEndAtAfter(productId, LocalDateTime.now())) {
            throw new BadRequestException("Already promoted");
        }

        ProductPromotion req = ProductPromotion.builder()
                .seller(seller)
                .product(product)
                .status(PromotionStatus.PENDING)
                .durationDays(durationDays)
                .createdAt(LocalDateTime.now())
                .build();

        return toResponse(promotionRepo.save(req));
    }

    public Page<PromotionResponse> getSellerRequests(String sellerEmail, int page, int perPage) {
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new BadRequestException("Seller not found"));

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), perPage, Sort.by("createdAt").descending());
        return promotionRepo.findBySeller_Id(seller.getId(), pageable).map(this::toResponse);
    }

    public Page<PromotionResponse> getPendingForAdmin(int page, int perPage) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), perPage, Sort.by("createdAt").descending());
        return promotionRepo.findByStatus(PromotionStatus.PENDING, pageable).map(this::toResponse);
    }

    @Transactional
    public void review(Long reqId, String adminEmail, boolean approve, LocalDateTime startOverride) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new BadRequestException("Admin not found"));

        if (admin.getType() != UserType.ADMIN) {
            throw new UnauthorizedException("Only admins can review promotion requests");
        }

        ProductPromotion req = promotionRepo.findById(reqId)
                .orElseThrow(() -> new BadRequestException("Request not found"));

        if (req.getStatus() != PromotionStatus.PENDING) {
            throw new BadRequestException("Already reviewed");
        }

        req.setDecidedBy(admin);
        req.setDecidedAt(LocalDateTime.now());

        if (!approve) {
            req.setStatus(PromotionStatus.REJECTED);
        } else {
            req.setStatus(PromotionStatus.APPROVED);
            LocalDateTime start = (startOverride != null) ? startOverride : LocalDateTime.now();
            req.setStartAt(start);
            req.setEndAt(start.plusDays(req.getDurationDays()));
        }

        promotionRepo.save(req);
    }

    public boolean isProductFeatured(Long productId) {
        return !promotionRepo.findActiveByProduct(productId, LocalDateTime.now()).isEmpty();
    }
}
