package geekcode.takatuf.dto.category;

import org.springframework.web.multipart.MultipartFile;

import lombok.*;

public class CategoryDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryRequest {
        private String name;
        private String description;
        private MultipartFile image;
        private Boolean active;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryResponse {
        private Long id;
        private String name;
        private String description;
        private String image;
        private Boolean active;
    }
}
