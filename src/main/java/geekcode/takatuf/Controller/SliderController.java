package geekcode.takatuf.Controller;

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

    @PostMapping("/add")
    public ResponseEntity<SliderResponse> createSlider(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SliderRequest request) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        SliderResponse response = sliderService.createSlider(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<SliderResponse> updateSlider(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SliderRequest request) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        SliderResponse response = sliderService.updateSlider(id, userDetails.getUsername(), request);
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

    @GetMapping("/all")
    public ResponseEntity<List<SliderResponse>> getAllSliders(
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        List<SliderResponse> sliders = sliderService.getAllSliders();
        return ResponseEntity.ok(sliders);
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
