package geekcode.takatuf.Service;

import geekcode.takatuf.dto.PaginatedResponse;
import geekcode.takatuf.dto.section.SectionResponse;
import geekcode.takatuf.dto.slider.SliderRequest;
import geekcode.takatuf.dto.slider.SliderResponse;
import geekcode.takatuf.dto.slider.SliderSortRequest;
import geekcode.takatuf.Entity.*;

import org.springframework.data.domain.*;
import geekcode.takatuf.Exception.Types.BadRequestException;
import geekcode.takatuf.Exception.Types.ResourceNotFoundException;
import geekcode.takatuf.Repository.CategoryRepository;
import geekcode.takatuf.Repository.ProductRepository;
import geekcode.takatuf.Repository.SliderRepository;
import geekcode.takatuf.Repository.StoreRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;
import java.io.IOException;
import java.nio.file.*;

@Service
@RequiredArgsConstructor
public class SliderService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final SliderRepository sliderRepository;
    private final CategoryRepository categoryRepository;

    public SliderResponse createSlider(String username, SliderRequest request) {
        String imageUrl = saveImage(request.getImage());

        Slider.SliderBuilder builder = Slider.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(imageUrl)
                .type(request.getType())
                .active(request.getActive() != null ? request.getActive() : true)
                .priority(request.getPriority() != null ? request.getPriority() : 0)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate());

        switch (request.getType().toUpperCase()) {
            case "STORE" -> {
                Store store = storeRepository.findById(request.getTargetId())
                        .orElseThrow(() -> new BadRequestException("Store not found with id: " + request.getTargetId()));
                builder.store(store);
            }
            case "PRODUCT" -> {
                Product product = productRepository.findById(request.getTargetId())
                        .orElseThrow(() -> new BadRequestException("Product not found with id: " + request.getTargetId()));
                builder.product(product);
            }
            case "CATEGORY" -> {
                Category category = categoryRepository.findById(request.getTargetId())
                        .orElseThrow(() -> new BadRequestException("Category not found with id: " + request.getTargetId()));
                builder.category(category);
            }
            case "LINK" -> builder.linkUrl(request.getLinkUrl());
            default -> throw new BadRequestException("Invalid slider type: " + request.getType());
        }

        Slider slider = sliderRepository.save(builder.build());
        return mapToResponse(slider);
    }

    public SliderResponse updateSlider(Long id, String username, SliderRequest request) {
        Slider slider = sliderRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Slider not found."));

        if (request.getTitle() != null)
            slider.setTitle(request.getTitle());
        if (request.getDescription() != null)
            slider.setDescription(request.getDescription());
        if (request.getType() != null)
            slider.setType(request.getType());
        if (request.getPriority() != null)
            slider.setPriority(request.getPriority());
        if (request.getStartDate() != null)
            slider.setStartDate(request.getStartDate());
        if (request.getEndDate() != null)
            slider.setEndDate(request.getEndDate());
        if (request.getActive() != null)
            slider.setActive(request.getActive());

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String imageUrl = saveImage(request.getImage());
            slider.setImageUrl(imageUrl);
        }

        if (request.getType() != null) {
            switch (request.getType().toUpperCase()) {
                case "STORE" -> {
                    Store store = storeRepository.findById(request.getTargetId())
                            .orElseThrow(() -> new BadRequestException("Store not found"));
                    slider.setStore(store);
                    slider.setProduct(null);
                    slider.setCategory(null);
                    slider.setLinkUrl(null);
                }
                case "PRODUCT" -> {
                    Product product = productRepository.findById(request.getTargetId())
                            .orElseThrow(() -> new BadRequestException("Product not found"));
                    slider.setProduct(product);
                    slider.setStore(null);
                    slider.setCategory(null);
                    slider.setLinkUrl(null);
                }
                case "CATEGORY" -> {
                    Category category = categoryRepository.findById(request.getTargetId())
                            .orElseThrow(() -> new BadRequestException("Category not found"));
                    slider.setCategory(category);
                    slider.setProduct(null);
                    slider.setStore(null);
                    slider.setLinkUrl(null);
                }
                case "LINK" -> {
                    slider.setLinkUrl(request.getLinkUrl());
                    slider.setProduct(null);
                    slider.setStore(null);
                    slider.setCategory(null);
                }
                default -> throw new BadRequestException("Invalid slider type: " + request.getType());
            }
        }

        Slider updated = sliderRepository.save(slider);
        return mapToResponse(updated);
    }

    private String saveImage(MultipartFile image) {
        if (image == null || image.isEmpty())
            return null;

        try {
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/sliders/");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/sliders/")
                    .path(fileName)
                    .toUriString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }

    public SliderResponse getSliderById(Long id) {
        Slider slider = sliderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slider not found"));
        return mapToResponse(slider);
    }

   public PaginatedResponse<SliderResponse> getAllSlidersPaginated(int page, int perPage, String q, String sort, String sortDir) {
    try {
        Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), perPage, Sort.by(direction, sort));

        Page<Slider> slidersPage = (q != null && !q.trim().isEmpty())
                ? sliderRepository.findByTitleContainingIgnoreCase(q, pageable)
                : sliderRepository.findAll(pageable);

        List<SliderResponse> data = slidersPage.map(this::mapToResponse).getContent();
        return new PaginatedResponse<>(data, slidersPage.getTotalElements(), page, perPage);
    } catch (IllegalArgumentException e) {
        throw new BadRequestException("Invalid sort field: " + sort);
    }
}

    public List<SliderResponse> getAllSliders() {
        List<Slider> sliders = sliderRepository.findAll();
        return sliders.stream().map(this::mapToResponse).toList();
    }

    public void deleteSlider(Long id, String username) {
        Slider slider = sliderRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Slider not found."));
        sliderRepository.delete(slider);
    }

    public void sortSliders(String username, List<SliderSortRequest> sortRequests) {
        for (SliderSortRequest sort : sortRequests) {
            Slider slider = sliderRepository.findById(sort.getId())
                    .orElseThrow(() -> new BadRequestException("Slider not found."));
            slider.setPriority(sort.getPriority());
            sliderRepository.save(slider);
        }
    }

    private SliderResponse mapToResponse(Slider slider) {
        Long targetId = null;
        String linkUrl = null;

        if (slider.getProduct() != null) {
            targetId = slider.getProduct().getId();
        } else if (slider.getStore() != null) {
            targetId = slider.getStore().getId();
        } else if (slider.getCategory() != null) {
            targetId = slider.getCategory().getId();
        } else if (slider.getLinkUrl() != null) {
            linkUrl = slider.getLinkUrl();
        }

        return SliderResponse.builder()
                .id(slider.getId())
                .title(slider.getTitle())
                .description(slider.getDescription())
                .imageUrl(slider.getImageUrl())
                .type(slider.getType())
                .active(slider.isActive())
                .priority(slider.getPriority())
                .startDate(slider.getStartDate())
                .endDate(slider.getEndDate())
                .targetId(targetId)
                .linkUrl(linkUrl)
                .build();
    }
}
