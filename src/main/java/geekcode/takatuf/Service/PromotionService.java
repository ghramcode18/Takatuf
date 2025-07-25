package geekcode.takatuf.Service;

import geekcode.takatuf.Entity.*;
import geekcode.takatuf.Enums.*;
import geekcode.takatuf.Exception.Types.BadRequestException;
import geekcode.takatuf.Repository.*;
import geekcode.takatuf.dto.promotion.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final StoreRepository storeRepository;

    private final String uploadDir = "uploads/promotions/";

    public PromotionResponse createPromotion(PromotionRequest request) {
        validateDates(request.getStartDate(), request.getEndDate());

        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new BadRequestException("Store not found"));

        String imageUrl = request.getImage() != null && !request.getImage().isEmpty()
                ? saveImage(request.getImage())
                : null;

        Promotion promotion = Promotion.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .discountPercentage(
                        request.getDiscountPercentage() != null ? BigDecimal.valueOf(request.getDiscountPercentage())
                                : null)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .active(request.getActive())
                .storeId(store.getId())
                .targetType(validateTargetType(request.getTargetType()))
                .imageUrl(imageUrl)
                .build();

        return mapToResponse(promotionRepository.save(promotion));
    }

    public PromotionResponse updatePromotion(Long id, PromotionRequest request) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Promotion not found"));

        if (request.getStartDate() != null && request.getEndDate() != null) {
            validateDates(request.getStartDate(), request.getEndDate());
        }

        if (request.getStoreId() != null) {
            Store store = storeRepository.findById(request.getStoreId())
                    .orElseThrow(() -> new BadRequestException("Store not found"));
            promotion.setStoreId(store.getId());
        }

        if (request.getTitle() != null && !request.getTitle().isBlank())
            promotion.setTitle(request.getTitle());

        if (request.getDescription() != null && !request.getDescription().isBlank())
            promotion.setDescription(request.getDescription());

        if (request.getDiscountPercentage() != null)
            promotion.setDiscountPercentage(BigDecimal.valueOf(request.getDiscountPercentage()));

        if (request.getStartDate() != null)
            promotion.setStartDate(request.getStartDate());

        if (request.getEndDate() != null)
            promotion.setEndDate(request.getEndDate());

        if (request.getActive() != null)
            promotion.setActive(request.getActive());

        if (request.getTargetType() != null)
            promotion.setTargetType(validateTargetType(request.getTargetType()));

        if (request.getImage() != null && !request.getImage().isEmpty())
            promotion.setImageUrl(saveImage(request.getImage()));

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

    public List<PromotionResponse> getPromotionsByActiveStatus(Boolean active) {
        LocalDateTime now = LocalDateTime.now();

        List<Promotion> promotions = (active == null)
                ? promotionRepository.findAll()
                // : promotionRepository.findByActiveAndStartDateBeforeAndEndDateAfter(active,
                // now, now);
                : promotionRepository.findByActive(active);
        return promotions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void deletePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Promotion not found"));
        promotionRepository.delete(promotion);
    }

    private void validateDates(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new BadRequestException("End date must be after start date.");
        }
    }

    private PromotionTargetType validateTargetType(PromotionTargetType type) {
        if (type == null) {
            throw new BadRequestException("Target type is required.");
        }
        try {
            return PromotionTargetType.valueOf(type.name());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid target type.");
        }
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
                .imageUrl(promotion.getImageUrl())
                .build();
    }

    private String saveImage(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/promotions/")
                    .path(fileName)
                    .toUriString();

        } catch (IOException e) {
            throw new BadRequestException("Failed to save promotion image");
        }
    }
}
