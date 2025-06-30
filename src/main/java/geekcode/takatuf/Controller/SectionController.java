package geekcode.takatuf.Controller;

import geekcode.takatuf.Service.SectionService;
import geekcode.takatuf.dto.section.SectionRequest;
import geekcode.takatuf.dto.section.SectionResponse;
import geekcode.takatuf.dto.section.SectionSortRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sections")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

    @PostMapping("/add")
    public ResponseEntity<SectionResponse> createSection(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SectionRequest request) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(sectionService.createSection(userDetails.getUsername(), request));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<SectionResponse> updateSection(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SectionRequest request) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(sectionService.updateSection(id, userDetails.getUsername(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SectionResponse> getSectionById(@PathVariable Long id) {
        return ResponseEntity.ok(sectionService.getSectionById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<SectionResponse>> getAllSections() {
        return ResponseEntity.ok(sectionService.getAllSections());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSection(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        sectionService.deleteSection(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sort")
    public ResponseEntity<Void> sortSections(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody List<SectionSortRequest> sortRequests) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        sectionService.sortSections(userDetails.getUsername(), sortRequests);
        return ResponseEntity.ok().build();
    }
}
