package geekcode.takatuf.Controller;

import geekcode.takatuf.Service.SectionService;
import geekcode.takatuf.dto.PaginatedResponse;
import geekcode.takatuf.dto.section.SectionItemSortRequest;
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

    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<SectionResponse> createSection(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute SectionRequest request) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(sectionService.createSection(userDetails.getUsername(), request));
    }

    @PostMapping(value = "/update/{id}", consumes = "multipart/form-data")
    public ResponseEntity<SectionResponse> updateSection(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute SectionRequest request) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(sectionService.updateSection(id, userDetails.getUsername(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SectionResponse> getSectionById(@PathVariable Long id) {
        return ResponseEntity.ok(sectionService.getSectionById(id));
    }

    @GetMapping("/paginated")
    public ResponseEntity<PaginatedResponse<SectionResponse>> getPaginatedSections(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "per_page", defaultValue = "10") int perPage,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "ASC") String sortDir,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null)
            return ResponseEntity.status(401).build();

        PaginatedResponse<SectionResponse> response = sectionService.getAllSectionsPaginated(
                page, perPage, q, sort, sortDir);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SectionResponse>> getAllSections() {
        List<SectionResponse> sections = sectionService.getAllSections();
        return ResponseEntity.ok(sections);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSection(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        sectionService.deleteSection(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sort")
    public ResponseEntity<Void> sortSections(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody List<SectionSortRequest> sortRequests) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        sectionService.sortSections(userDetails.getUsername(), sortRequests);
        return ResponseEntity.ok().build();
    }

@PostMapping("/{sectionId}/items/sort")
public ResponseEntity<Void> sortSectionItems(
        @PathVariable Long sectionId,
        @RequestBody List<SectionItemSortRequest> sortRequests,
        @AuthenticationPrincipal UserDetails userDetails) {

    if (userDetails == null) return ResponseEntity.status(401).build();

    sectionService.sortSectionItems(sectionId, sortRequests);
    return ResponseEntity.ok().build();
}
}
