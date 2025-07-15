package geekcode.takatuf.Service;

import geekcode.takatuf.dto.section.SectionRequest;
import geekcode.takatuf.dto.section.SectionResponse;
import geekcode.takatuf.dto.section.SectionSortRequest;
import geekcode.takatuf.Entity.Section;
import geekcode.takatuf.Entity.SectionItem;
import geekcode.takatuf.Entity.Product;
import geekcode.takatuf.Entity.Store;
import geekcode.takatuf.Exception.Types.BadRequestException;
import geekcode.takatuf.Exception.Types.ResourceNotFoundException;
import geekcode.takatuf.Repository.ProductRepository;
import geekcode.takatuf.Repository.SectionRepository;
import geekcode.takatuf.Repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import geekcode.takatuf.dto.PaginatedResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;

    public SectionResponse createSection(String username, SectionRequest request) {
        String imageUrl = saveImage(request.getImage());

        Section section = Section.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .imageUrl(imageUrl)
                .active(request.getActive())
                .sortOrder(request.getSortOrder())
                .build();

        if (request.getIds() != null && !request.getIds().isEmpty()) {
            List<SectionItem> items = request.getIds().stream().map(itemId -> {
                SectionItem.SectionItemBuilder itemBuilder = SectionItem.builder().section(section);
                switch (request.getType().toUpperCase()) {
                    case "PRODUCT" -> itemBuilder.product(
                            productRepository.findById(itemId)
                                    .orElseThrow(() -> new BadRequestException("Product not found: " + itemId)));
                    case "STORE" -> itemBuilder.store(
                            storeRepository.findById(itemId)
                                    .orElseThrow(() -> new BadRequestException("Store not found: " + itemId)));
                    default -> throw new BadRequestException("Invalid type for section: " + request.getType());
                }
                return itemBuilder.build();
            }).toList();

            section.setItems(items);
        }

        Section saved = sectionRepository.save(section);
        return mapToResponse(saved);
    }

    public SectionResponse updateSection(Long id, String username, SectionRequest request) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Section not found."));

        if (request.getName() != null)
            section.setName(request.getName());
        if (request.getDescription() != null)
            section.setDescription(request.getDescription());
        if (request.getType() != null)
            section.setType(request.getType());
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String imageUrl = saveImage(request.getImage());
            section.setImageUrl(imageUrl);
        }
        if (request.getActive() != null)
            section.setActive(request.getActive());
        if (request.getSortOrder() != null)
            section.setSortOrder(request.getSortOrder());

        if (request.getIds() != null) {
            section.getItems().clear();

            List<SectionItem> updatedItems = request.getIds().stream().map(itemId -> {
                SectionItem.SectionItemBuilder itemBuilder = SectionItem.builder().section(section);
                switch (request.getType().toUpperCase()) {
                    case "PRODUCT" -> itemBuilder.product(
                            productRepository.findById(itemId)
                                    .orElseThrow(() -> new BadRequestException("Product not found: " + itemId)));
                    case "STORE" -> itemBuilder.store(
                            storeRepository.findById(itemId)
                                    .orElseThrow(() -> new BadRequestException("Store not found: " + itemId)));
                    default -> throw new BadRequestException("Invalid section type: " + request.getType());
                }
                return itemBuilder.build();
            }).toList();

            section.getItems().addAll(updatedItems);
        }

        Section updated = sectionRepository.save(section);
        return mapToResponse(updated);
    }

    private String saveImage(MultipartFile image) {
        try {
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/sections/");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/sections/")
                    .path(fileName)
                    .toUriString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to save section image", e);
        }
    }

    public SectionResponse getSectionById(Long id) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));
        return mapToResponse(section);
    }

    public PaginatedResponse<SectionResponse> getAllSectionsPaginated(int page, int perPage, String q, String sort,
            String sortDir) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), perPage, Sort.by(direction, sort));

        Page<Section> pageResult;
        if (q != null && !q.trim().isEmpty()) {
            pageResult = sectionRepository.findByNameContainingIgnoreCase(q, pageable);
        } else {
            pageResult = sectionRepository.findAll(pageable);
        }

        List<SectionResponse> data = pageResult.map(this::mapToResponse).getContent();
        return new PaginatedResponse<>(data, pageResult.getTotalElements(), page, perPage);
    }

    public List<SectionResponse> getAllSections() {
        List<Section> sections = sectionRepository.findAll();
        return sections.stream().map(this::mapToResponse).toList();
    }

    public void deleteSection(Long id, String username) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Section not found."));
        sectionRepository.delete(section);
    }

    public void sortSections(String username, List<SectionSortRequest> sortRequests) {
        for (SectionSortRequest sort : sortRequests) {
            Section section = sectionRepository.findById(sort.getId())
                    .orElseThrow(() -> new BadRequestException("Section not found."));
            section.setSortOrder(sort.getSortOrder());
            sectionRepository.save(section);
        }
    }

    private SectionResponse mapToResponse(Section section) {
        List<Long> ids = section.getItems() != null
                ? section.getItems().stream()
                        .map(item -> {
                            if (item.getProduct() != null)
                                return item.getProduct().getId();
                            if (item.getStore() != null)
                                return item.getStore().getId();
                            return null;
                        })
                        .filter(idVal -> idVal != null)
                        .collect(Collectors.toList())
                : List.of();

        return SectionResponse.builder()
                .id(section.getId())
                .name(section.getName())
                .description(section.getDescription())
                .type(section.getType())
                .imageUrl(section.getImageUrl())
                .active(section.getActive())
                .sortOrder(section.getSortOrder())
                .createdAt(section.getCreatedAt())
                .updatedAt(section.getUpdatedAt())
                .ids(ids)
                .build();
    }
}
