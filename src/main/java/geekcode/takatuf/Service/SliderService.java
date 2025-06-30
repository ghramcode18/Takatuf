package geekcode.takatuf.Service;

import geekcode.takatuf.dto.slider.SliderRequest;
import geekcode.takatuf.dto.slider.SliderResponse;
import geekcode.takatuf.dto.slider.SliderSortRequest;
import geekcode.takatuf.Entity.Slider;
import geekcode.takatuf.Exception.Types.BadRequestException;
import geekcode.takatuf.Exception.Types.ResourceNotFoundException;
import geekcode.takatuf.Repository.SliderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SliderService {

    private final SliderRepository sliderRepository;

    public SliderResponse createSlider(String username, SliderRequest request) {
        Slider slider = Slider.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .targetUrl(request.getTargetUrl())
                .type(request.getType())
                .active(request.isActive())
                .priority(request.getPriority())
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
        if (request.getImageUrl() != null)
            slider.setImageUrl(request.getImageUrl());
        if (request.getTargetUrl() != null)
            slider.setTargetUrl(request.getTargetUrl());
        if (request.getType() != null)
            slider.setType(request.getType());
        if (request.getPriority() != null)
            slider.setPriority(request.getPriority());
        if (request.getStartDate() != null)
            slider.setStartDate(request.getStartDate());
        if (request.getEndDate() != null)
            slider.setEndDate(request.getEndDate());

        slider.setActive(request.isActive()); 

        Slider updated = sliderRepository.save(slider);
        return mapToResponse(updated);
    }

    public SliderResponse getSliderById(Long id) {
        Slider slider = sliderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slider not found"));
        return mapToResponse(slider);
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
