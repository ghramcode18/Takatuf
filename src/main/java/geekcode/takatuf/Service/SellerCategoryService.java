package geekcode.takatuf.Service;

import geekcode.takatuf.dto.*;
import geekcode.takatuf.dto.category.CategoryDto;
import geekcode.takatuf.Entity.*;
import org.springframework.data.domain.*;
import geekcode.takatuf.Exception.Types.BadRequestException;
import geekcode.takatuf.Exception.Types.ResourceNotFoundException;
import geekcode.takatuf.Exception.Types.UnauthorizedException;
import geekcode.takatuf.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import geekcode.takatuf.Enums.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerCategoryService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final SellerCategoryRepository sellerCategoryRepository;

    @Transactional
    public void updateSellerCategories(Long sellerId, List<Long> categoryIds) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        if (seller.getType() != UserType.SELLER && seller.getType() != UserType.ADMIN) {
            throw new BadRequestException("Only sellers and admins can update categories");
        }
        List<Category> validCategories = categoryRepository.findAllById(categoryIds);
        if (validCategories.size() != categoryIds.size()) {
            List<Long> foundIds = validCategories.stream().map(Category::getId).toList();
            List<Long> missingIds = categoryIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new ResourceNotFoundException("Categories not found: " + missingIds);
        }

        sellerCategoryRepository.deleteBySellerId(sellerId);

        List<SellerCategory> toSave = validCategories.stream().map(category -> SellerCategory.builder()
                .seller(seller)
                .category(category)
                .createdAt(LocalDateTime.now())
                .build()).toList();

        sellerCategoryRepository.saveAll(toSave);
    }

    public List<CategoryDto.CategoryResponse> getSellerCategories(Long sellerId) {
        return sellerCategoryRepository.findBySeller_Id(sellerId).stream()
                .map(sc -> {
                    Category category = sc.getCategory();
                    return new CategoryDto.CategoryResponse(
                            category.getId(),
                            category.getName(),
                            category.getDescription(),
                            category.getImage(),
                            category.getActive());
                })
                .toList();
    }

    public Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email))
                .getId();
    }

}