package geekcode.takatuf.Service;

import geekcode.takatuf.dto.PaginatedResponse;
import geekcode.takatuf.dto.section.SectionResponse;
import geekcode.takatuf.dto.slider.SliderRequest;
import geekcode.takatuf.dto.slider.SliderResponse;
import geekcode.takatuf.dto.slider.SliderSortRequest;
import geekcode.takatuf.Entity.Section;
import geekcode.takatuf.Entity.Slider;
import org.springframework.data.domain.*;
import geekcode.takatuf.Exception.Types.BadRequestException;
import geekcode.takatuf.Exception.Types.ResourceNotFoundException;
import geekcode.takatuf.Repository.SliderRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SliderService {

    private final SliderRepository sliderRepository;

    public SliderResponse createSlider(String username, SliderRequest request) {
        String imageUrl = saveImage(request.getImage());
        String targetUrl = resolveTargetUrl(request);

        Slider slider = Slider.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(imageUrl)
                .targetUrl(targetUrl)
                .type(request.getType())
                .active(request.getActive() != null ? request.getActive() : true)
                .priority(request.getPriority() != null ? request.getPriority() : 0)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        Slider saved = sliderRepository.save(slider);
        return mapToResponse(saved);
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

        if (request.getType() != null || request.getTargetId() != null || request.getLinkUrl() != null) {
            String targetUrl = resolveTargetUrl(request);
            slider.setTargetUrl(targetUrl);
        }

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String imageUrl = saveImage(request.getImage());
            slider.setImageUrl(imageUrl);
        }

        Slider updated = sliderRepository.save(slider);
        return mapToResponse(updated);
    }

    private String resolveTargetUrl(SliderRequest request) {
        String type = request.getType();
        if (type == null)
            return null;

        return switch (type.toUpperCase()) {
            case "STORE" -> request.getTargetId() != null ? "/stores/" + request.getTargetId() : null;
            case "PRODUCT" -> request.getTargetId() != null ? "/products/" + request.getTargetId() : null;
            case "LINK", "NONE" -> request.getLinkUrl();
            default -> null;
        };
    }

    private String saveImage(MultipartFile image) {
        if (image == null || image.isEmpty())
            return null;

        try {
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/slider/");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/slider/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }

    public SliderResponse getSliderById(Long id) {
        Slider slider = sliderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slider not found"));
        return mapToResponse(slider);
    }

    public PaginatedResponse<SliderResponse> getAllSlidersPaginated(int page, int perPage, String sort,
            String sortDir) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), perPage, Sort.by(direction, sort));

        Page<Slider> slidersPage = sliderRepository.findAll(pageable);
        List<SliderResponse> data = slidersPage.map(this::mapToResponse).getContent();

        return new PaginatedResponse<>(data, slidersPage.getTotalElements(), page, perPage);
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
        return SliderResponse.builder()
                .id(slider.getId())
                .title(slider.getTitle())
                .description(slider.getDescription())
                .imageUrl(slider.getImageUrl())
                .targetUrl(slider.getTargetUrl())
                .type(slider.getType())
                .active(slider.isActive())
                .priority(slider.getPriority())
                .startDate(slider.getStartDate())
                .endDate(slider.getEndDate())
                .build();
    }
}
