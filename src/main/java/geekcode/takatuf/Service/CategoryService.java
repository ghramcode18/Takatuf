package geekcode.takatuf.Service;

import geekcode.takatuf.Entity.Category;
import geekcode.takatuf.Exception.Types.BadRequestException;
import geekcode.takatuf.Repository.CategoryRepository;
import geekcode.takatuf.dto.category.CategoryDto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse createCategory(CategoryRequest request) {
        validateCategoryRequest(request);

        String imagePath = saveImage(request.getImage());

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .image(imagePath)
                .active(Optional.ofNullable(request.getActive()).orElse(true))
                .build();

        return mapToResponse(categoryRepository.save(category));
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Category not found"));

        Optional.ofNullable(request.getName()).ifPresent(category::setName);
        Optional.ofNullable(request.getDescription()).ifPresent(category::setDescription);

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String imagePath = saveImage(request.getImage());
            category.setImage(imagePath);
        }

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
        if (request.getImage() == null || request.getImage().isEmpty()) {
            throw new BadRequestException("Category image is required");
        }
    }

    private String saveImage(MultipartFile image) {
        try {
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/categories/");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/categories/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save category image", e);
        }
    }
}
