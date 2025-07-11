package geekcode.takatuf.Controller;

import geekcode.takatuf.dto.PaginatedResponse;
import geekcode.takatuf.dto.slider.SliderRequest;
import geekcode.takatuf.dto.slider.SliderResponse;
import geekcode.takatuf.dto.slider.SliderSortRequest;
import geekcode.takatuf.Service.SliderService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sliders")
@RequiredArgsConstructor
public class SliderController {

    private final SliderService sliderService;

    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<SliderResponse> createSlider(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute SliderRequest request) {
        SliderResponse response = sliderService.createSlider(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/update/{id}", consumes = "multipart/form-data")
    public ResponseEntity<SliderResponse> updateSlider(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @ModelAttribute SliderRequest request) {
        SliderResponse response = sliderService.updateSlider(id, userDetails.getUsername(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/paginated")
    public ResponseEntity<PaginatedResponse<SliderResponse>> getPaginatedSliders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int perPage,
            @RequestParam(defaultValue = "priority") String sort,
            @RequestParam(defaultValue = "ASC") String sortDir) {

        PaginatedResponse<SliderResponse> response = sliderService.getAllSlidersPaginated(page, perPage, sort, sortDir);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SliderResponse> getSliderById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        SliderResponse response = sliderService.getSliderById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSlider(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        sliderService.deleteSlider(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<SliderResponse>> getAllSliders() {
        List<SliderResponse> sliders = sliderService.getAllSliders();
        return ResponseEntity.ok(sliders);
    }

    @PostMapping("/sort")
    public ResponseEntity<Void> sortSliders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody List<SliderSortRequest> sortRequests) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        sliderService.sortSliders(userDetails.getUsername(), sortRequests);
        return ResponseEntity.ok().build();
    }
}
