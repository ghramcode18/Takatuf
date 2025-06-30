package geekcode.takatuf.Service;

import geekcode.takatuf.dto.section.SectionRequest;
import geekcode.takatuf.dto.section.SectionResponse;
import geekcode.takatuf.dto.section.SectionSortRequest;
import geekcode.takatuf.Entity.Section;
import geekcode.takatuf.Exception.Types.BadRequestException;
import geekcode.takatuf.Exception.Types.ResourceNotFoundException;
import geekcode.takatuf.Repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;

    public SectionResponse createSection(String username, SectionRequest request) {
        Section section = Section.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .imageUrl(request.getImage())
                .active(request.getActive())
                .sortOrder(request.getSortOrder())
                .build();

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
        if (request.getImage() != null)
            section.setImageUrl(request.getImage());
        if (request.getActive() != null)
            section.setActive(request.getActive());
        if (request.getSortOrder() != null)
            section.setSortOrder(request.getSortOrder());

        Section updated = sectionRepository.save(section);
        return mapToResponse(updated);
    }

    public SectionResponse getSectionById(Long id) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));
        return mapToResponse(section);
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
                .build();
    }
}
