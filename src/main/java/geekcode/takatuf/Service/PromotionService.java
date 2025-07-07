package geekcode.takatuf.Service;

import geekcode.takatuf.Entity.*;
import geekcode.takatuf.Exception.Types.BadRequestException;
import geekcode.takatuf.Repository.*;
import geekcode.takatuf.dto.promotion.PromotionDto.PromotionRequest;
import geekcode.takatuf.dto.promotion.PromotionDto.PromotionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final StoreRepository storeRepository;

    public PromotionResponse createPromotion(PromotionRequest request) {
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new BadRequestException("Store not found"));

        Promotion promotion = Promotion.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .discountPercentage(request.getDiscountPercentage())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .active(request.getActive())
                .storeId(store.getId())
                .targetType(request.getTargetType())
                .build();

        return mapToResponse(promotionRepository.save(promotion));
    }

    public PromotionResponse updatePromotion(Long id, PromotionRequest request) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Promotion not found"));

        if (request.getStoreId() != null) {
            Store store = storeRepository.findById(request.getStoreId())
                    .orElseThrow(() -> new BadRequestException("Store not found"));
            promotion.setStoreId(store.getId());
        }

        if (request.getTitle() != null)
            promotion.setTitle(request.getTitle());
        if (request.getDescription() != null)
            promotion.setDescription(request.getDescription());
        if (request.getDiscountPercentage() != null)
            promotion.setDiscountPercentage(request.getDiscountPercentage());
        if (request.getStartDate() != null)
            promotion.setStartDate(request.getStartDate());
        if (request.getEndDate() != null)
            promotion.setEndDate(request.getEndDate());
        if (request.getActive() != null)
            promotion.setActive(request.getActive());
        if (request.getTargetType() != null)
            promotion.setTargetType(request.getTargetType());

        return mapToResponse(promotionRepository.save(promotion));
    }

    public PromotionResponse getPromotionById(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Promotion not found"));
        return mapToResponse(promotion);
    }

    public List<PromotionResponse> getAllPromotions() {
        return promotionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    public List<PromotionResponse> getActivePromotions() {
        LocalDateTime now = LocalDateTime.now();
        return promotionRepository.findByActiveTrueAndStartDateBeforeAndEndDateAfter(now, now)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PromotionResponse mapToResponse(Promotion promotion) {
        return PromotionResponse.builder()
                .id(promotion.getId())
                .title(promotion.getTitle())
                .description(promotion.getDescription())
                .discountPercentage(promotion.getDiscountPercentage())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .active(promotion.getActive())
                .storeId(promotion.getStoreId())
                .storeName(promotion.getStore() != null ? promotion.getStore().getName() : null)
                .targetType(promotion.getTargetType())
                .build();
    }
}
