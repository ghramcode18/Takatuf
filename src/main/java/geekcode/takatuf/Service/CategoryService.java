package geekcode.takatuf.Service;

import geekcode.takatuf.Entity.Category;
import geekcode.takatuf.Exception.Types.BadRequestException;
import geekcode.takatuf.Repository.CategoryRepository;
import geekcode.takatuf.Repository.UserRepository;
import geekcode.takatuf.dto.PaginatedResponse;
import geekcode.takatuf.dto.category.CategoryDto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import geekcode.takatuf.Enums.UserType;
import geekcode.takatuf.Entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final String uploadDir = "uploads/categories/";

    public CategoryResponse createCategory(CategoryRequest request) {
        validateCategoryRequest(request);

        String imageUrl = saveImage(request.getImage());

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .image(imageUrl)
                .active(Optional.ofNullable(request.getActive()).orElse(true))
                .build();

        return mapToResponse(categoryRepository.save(category));
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Category not found"));

        Optional.ofNullable(request.getName()).ifPresent(newName -> {
            if (!newName.equalsIgnoreCase(category.getName()) &&
                    categoryRepository.existsByNameIgnoreCase(newName)) {
                throw new BadRequestException("Category name already exists");
            }
            category.setName(newName);
        });

        Optional.ofNullable(request.getDescription()).ifPresent(category::setDescription);

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            deleteImageIfExists(category.getImage());
            String imageUrl = saveImage(request.getImage());
            category.setImage(imageUrl);
        }

        Optional.ofNullable(request.getActive()).ifPresent(category::setActive);

        return mapToResponse(categoryRepository.save(category));
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Category not found"));

        deleteImageIfExists(category.getImage());
        categoryRepository.deleteById(id);
    }

    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = isSeller()
                ? categoryRepository.findAll()
                : categoryRepository.findCustomerVisible(null, Pageable.unpaged()).getContent();

        return categories.stream().map(this::mapToResponse).toList();
    }

    public PaginatedResponse<CategoryResponse> getCategoriesPaginated(
            int page,
            int perPage,
            String q,
            String sort,
            String sortDir) {

        Sort.Direction direction = sortDir != null && sortDir.equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        String sortField = (sort == null || sort.isBlank()) ? "name" : sort;

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), perPage, Sort.by(direction, sortField));

        Page<Category> pageResult = isSeller()
                ? categoryRepository.findAllByNameLike(q, pageable) // البائع يرى كل الفئات
                : categoryRepository.findCustomerVisible(q, pageable); // المشتري يرى فقط الفئات غير الفارغة

        List<CategoryResponse> data = pageResult.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return new PaginatedResponse<>(data, pageResult.getTotalElements(), page, perPage);
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
        if (isBlank(request.getName())) {
            throw new BadRequestException("Category name is required");
        }

        if (request.getImage() == null || request.getImage().isEmpty()) {
            throw new BadRequestException("Category image is required");
        }

        boolean exists = categoryRepository.existsByNameIgnoreCase(request.getName());
        if (exists) {
            throw new BadRequestException("Category name already exists");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
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
                    .path("/uploads/categories/")
                    .path(fileName)
                    .toUriString();

        } catch (IOException e) {
            throw new BadRequestException("Failed to save category image");
        }
    }

    private void deleteImageIfExists(String imageUrl) {
        if (imageUrl == null || !imageUrl.contains("/"))
            return;

        try {
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path path = Paths.get(uploadDir).resolve(fileName);
            Files.deleteIfExists(path);
        } catch (IOException e) {
        }
    }

    private boolean isSeller() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)
            return false;

        String email = auth.getName();
        return userRepository.findByEmail(email)
                .map(u -> u.getType() == UserType.SELLER)
                .orElse(false);
    }
}