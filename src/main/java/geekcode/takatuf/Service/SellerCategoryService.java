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

    public void addCategoryToSeller(Long sellerId, Long categoryId) {

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        if (seller.getType() != UserType.SELLER && seller.getType() != UserType.ADMIN) {
            throw new BadRequestException("Only sellers and admins can add categories");
        }

        if (sellerCategoryRepository.existsBySellerIdAndCategoryId(sellerId, categoryId)) {
            throw new BadRequestException("Category already assigned to seller.");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        SellerCategory sc = SellerCategory.builder()
                .seller(seller)
                .category(category)
                .createdAt(LocalDateTime.now())
                .build();

        sellerCategoryRepository.save(sc);
    }

    public void removeCategoryFromSeller(Long sellerId, Long categoryId) {
        SellerCategory sc = sellerCategoryRepository.findBySeller_Id(sellerId).stream()
                .filter(item -> item.getCategory().getId().equals(categoryId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Category not assigned to seller"));

        sellerCategoryRepository.delete(sc);
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
                    category.getActive()
                );
            })
            .toList();
}

    public Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email))
                .getId();
    }

    public void addMultipleCategoriesToSeller(Long sellerId, List<Long> categoryIds) {
        for (Long categoryId : categoryIds) {
            addCategoryToSeller(sellerId, categoryId);
        }
    }

    public void removeMultipleCategoriesFromSeller(Long sellerId, List<Long> categoryIds) {
        for (Long categoryId : categoryIds) {
            removeCategoryFromSeller(sellerId, categoryId);
        }
    }

}
