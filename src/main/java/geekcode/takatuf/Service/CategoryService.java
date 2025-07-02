package geekcode.takatuf.Service;

import geekcode.takatuf.Entity.Category;
import geekcode.takatuf.Exception.Types.BadRequestException;
import geekcode.takatuf.Repository.CategoryRepository;
import geekcode.takatuf.dto.category.CategoryDto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse createCategory(CategoryRequest request) {
        validateCategoryRequest(request);

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .image(request.getImage())
                .active(Optional.ofNullable(request.getActive()).orElse(true)) // default to true
                .build();

        return mapToResponse(categoryRepository.save(category));
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Category not found"));

        Optional.ofNullable(request.getName()).ifPresent(category::setName);
        Optional.ofNullable(request.getDescription()).ifPresent(category::setDescription);
        Optional.ofNullable(request.getImage()).ifPresent(category::setImage);
        Optional.ofNullable(request.getActive()).ifPresent(category::setActive);

        return mapToResponse(categoryRepository.save(category));
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new BadRequestException("Category not found");
        }
        categoryRepository.deleteById(id);
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Category not found"));
        return mapToResponse(category);
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .image(category.getImage())
                .active(category.getActive())
                .build();
    }

    private void validateCategoryRequest(CategoryRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("Category name is required");
        }
        if (request.getImage() == null || request.getImage().isBlank()) {
            throw new BadRequestException("Category image is required");
        }
    }
}
